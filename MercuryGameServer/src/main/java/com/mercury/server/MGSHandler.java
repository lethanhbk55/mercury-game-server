package com.mercury.server;

import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.mario.entity.message.SocketMessage;
import com.mario.gateway.socket.SocketMessageType;
import com.mario.gateway.socket.SocketSession;
import com.mercury.server.annotation.AnnotationLoader;
import com.mercury.server.api.PluginApiFactory;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.session.PingSessionManager;
import com.mercury.server.entity.session.SessionManager;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.MessageTypeNotHandling;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.extension.loader.ExtensionInitializer;
import com.mercury.server.extension.loader.ExtensionManager;
import com.mercury.server.interop.message.MGSInteropMessage;
import com.mercury.server.interop.message.MGSInteropMessageHelper;
import com.mercury.server.interop.message.impl.InteropPluginMessage;
import com.mercury.server.interop.message.impl.InteropRoomMessage;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageHelper;
import com.mercury.server.processor.ProcessorManager;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.utils.CCULog;
import com.mercury.server.utils.GhostHunter;
import com.mercury.server.utils.Monitor;
import com.mercury.server.utils.RoomHunter;
import com.nhb.common.data.PuElement;
import com.nhb.common.data.PuObjectRO;

public class MGSHandler extends BaseMessageHandler {
	private ZoneManager zoneManager;
	private PluginApiFactory factory;
	private ExtensionManager extensionManager;
	private ProcessorManager processorManager;
	private ExtensionInitializer extensionInitializer;
	private GhostHunter ghostHunter;
	private PingSessionManager pingSessionManager;
	private SessionManager sessionManager;
	private CCULog ccuLog;
	private Monitor monitor;
	private RoomHunter roomHunter;

	@Override
	public void init(PuObjectRO initParams) {
		zoneManager = new ZoneManager();
		extensionManager = new ExtensionManager();
		try {
			extensionManager.load();
		} catch (Exception e) {
			getLogger().error("error while load extension folder", e);
			throw new RuntimeException("load extension error", e);
		}

		factory = new PluginApiFactory(extensionManager, getApi(), zoneManager);

		processorManager = new ProcessorManager();
		try {
			processorManager.init(AnnotationLoader.load("com.mercury.server.processor.impl"), this);
		} catch (Exception e) {
			getLogger().error("error while register processor", e);
			throw new RuntimeException(e);
		}

		sessionManager = new SessionManager();

		extensionInitializer = new ExtensionInitializer(extensionManager, factory, zoneManager, processorManager,
				sessionManager);
		try {
			extensionInitializer.init();
		} catch (Exception e) {
			getLogger().error("error while init plugins", e);
			throw new RuntimeException("init extension error", e);
		}

		pingSessionManager = new PingSessionManager();

		int timeForPing = initParams.getInteger("idle", 30);
		ghostHunter = new GhostHunter(pingSessionManager, sessionManager, zoneManager, getApi(), timeForPing);
		ghostHunter.start();

		ccuLog = new CCULog(getApi(), zoneManager, sessionManager);
		ccuLog.start();

		monitor = new Monitor(getApi());
		monitor.start();

		roomHunter = new RoomHunter(zoneManager, getApi());
		roomHunter.start();
	}

	@Override
	public PuElement handle(Message message) {
		SocketMessage socketMessage = (SocketMessage) message;
		String sessionId = socketMessage.getSessionId();
		SocketMessageType socketMessageType = socketMessage.getSocketMessageType();
		SocketSession socketSession = getApi().getSocketSession(sessionId);

		switch (socketMessageType) {
		case OPENED:
			if (socketSession != null) {
				getLogger().info("[OPEN] socket from {} - sessionId: {}",
						socketSession.getRemoteAddress().getHostString(), sessionId);
			}
			pingSessionManager.updatePing(sessionId);
			break;
		case MESSAGE:
			return processMessage(message, sessionId);
		case CLOSED:
			getLogger().info("[CLOSE] sessionId: {}", sessionId);
			Zone zone = sessionManager.getZoneBySessionId(sessionId);
			if (zone != null) {
				zone.getUserManager().removeUserBySessionId(sessionId, UserDisconnectReason.UNKNOWN);
			} else {
				sessionManager.removeSession(sessionId);
			}
			break;
		default:
			break;
		}

		return null;
	}

	private PuElement processMessage(Message message, String sessionId) {
		MGSMessage mgsMessage = null;
		try {
			mgsMessage = MGSMessageHelper.deserilize(message.getData());
		} catch (Exception e) {
			throw new RuntimeException("create message instance error\nMessage: " + message.getData(), e);
		}

		if (mgsMessage != null && mgsMessage.getType() != null) {
			try {
				MGSResponse response = processorManager.processCommand(sessionId, mgsMessage);
				if (response != null) {
					SocketSession socketSession = getApi().getSocketSession(sessionId);
					if (socketSession != null) {
						try {
							socketSession.send(response.serialize());
						} catch (Exception e) {
							getLogger().error("send response to client failure, sessionId: {}", sessionId, e);
						}
					} else {
						getLogger().warn("Cannot send response to client, sessionId: {}", sessionId);
					}
				}
			} catch (MessageTypeNotHandling | ProcessMessageException e) {
				getLogger().error("processor message event error", e);
			}
		}

		return null;
	}

	@Override
	public void onServerReady() {
		for (Zone zone : this.zoneManager.getZones()) {
			zone.getPlugin().onServerReady();
			zone.getPluginManager().onServerReady();
		}
	}

	@Override
	public PuElement interop(PuElement requestParams) {
		try {
			MGSInteropMessage message = MGSInteropMessageHelper.deserialize(requestParams);
			String zoneName = message.getZoneName();
			Zone zone = zoneManager.getZone(zoneName);
			if (zone != null) {
				if (message instanceof InteropRoomMessage) {
					InteropRoomMessage roomMessage = (InteropRoomMessage) message;
					Room room = zone.getRoomManager().findRoomById(roomMessage.getRoomId());
					if (room != null) {
						room.getRoomPlugin().interop(roomMessage.getParams());
					}
				} else if (message instanceof InteropPluginMessage) {
					InteropPluginMessage pluginMessage = (InteropPluginMessage) message;
					zone.getPluginManager().interop(pluginMessage.getPluginName(), pluginMessage.getParams());
				}
			}
		} catch (Exception e) {
			getLogger().warn("deserialize interop message exception", e);
		}
		return null;
	}

	@Override
	public void destroy() throws Exception {
		if (ghostHunter != null) {
			ghostHunter.stop();
		}

		if (roomHunter != null) {
			roomHunter.stop();
		}

		if (zoneManager != null) {
			for (Zone zone : zoneManager.getZones()) {
				zone.stop();
			}
		}

		if (this.pingSessionManager != null) {
			this.pingSessionManager.shutdown();
		}

		if (this.sessionManager != null) {
			this.sessionManager.shutdown();
		}
	}

	public ZoneManager getZoneManager() {
		return this.zoneManager;
	}

	public PingSessionManager getPingManager() {
		return this.pingSessionManager;
	}

	public SessionManager getSessionManager() {
		return this.sessionManager;
	}
}
