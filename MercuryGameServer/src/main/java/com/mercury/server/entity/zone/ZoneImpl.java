package com.mercury.server.entity.zone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mario.api.MarioApi;
import com.mario.gateway.socket.SocketSession;
import com.mario.schedule.ScheduledCallback;
import com.mercury.server.callback.JoinRoomCallback;
import com.mercury.server.entity.room.AbstractRoom;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.room.RoomManager;
import com.mercury.server.entity.session.SessionManager;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.user.UserEvent;
import com.mercury.server.entity.user.UserManager;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.MGSException;
import com.mercury.server.navigator.JoinRoomNavigator;
import com.mercury.server.plugin.PluginManager;
import com.mercury.server.plugin.ZonePlugin;
import com.mercury.server.response.impl.LoginResponse;
import com.mercury.server.response.impl.LogoutResponse;
import com.mercury.server.schedule.MGSScheduledService;
import com.nhb.common.BaseLoggable;
import com.nhb.common.data.PuValue;
import com.nhb.eventdriven.Callable;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;

public class ZoneImpl extends BaseLoggable implements Zone {

	class AddUserEventHandler implements EventHandler {

		@Override
		public void onEvent(Event event) throws Exception {
			UserEvent userEvent = (UserEvent) event;
			User user = userEvent.getUser();
			getLogger().info("User {} loggged in and join to zone {}", user.getUsername(), ZoneImpl.this.getZoneName());
			sessionManager.addSessionToZone(user.getSessionId(), ZoneImpl.this);

			sendLoginSuccess(user);

			getScheduledService().execute(new ScheduledCallback() {

				@Override
				public void call() {
					try {
						zonePlugin.userLoggedIn(user);
					} catch (Exception e) {
						getLogger().error("Zone Plugin handle user logged in exception", e);
					}
				}
			});
		}
	}

	class RemoveUserEventHandler implements EventHandler {

		@Override
		public void onEvent(Event event) throws Exception {
			UserEvent userEvent = (UserEvent) event;
			User user = userEvent.getUser();
			UserDisconnectReason reason = userEvent.getReason();
			sessionManager.removeSession(user.getSessionId());

			getLogger().debug("remove user success {}", user.getUsername());
			ScheduledCallback callback = new ScheduledCallback() {

				@Override
				public void call() {
					try {
						Room lastJoinedRoom = user.getLastJoinedRoom();
						if (lastJoinedRoom != null && lastJoinedRoom instanceof AbstractRoom) {
							UserLeaveRoomReason leaveRoomReason = reason == UserDisconnectReason.KICKED
									? UserLeaveRoomReason.KICKED
									: UserLeaveRoomReason.LEAVE_ROOM;
							try {
								((AbstractRoom) lastJoinedRoom).leaveRoom(user, leaveRoomReason, null);
							} catch (MGSException e) {
								getLogger().warn("user {} disconnect to leave room", user.getUsername());
							}
						}
						sendLogoutSucess(user, reason);
						getLogger().info("[DISCONNECT] user {} disconnected from zone {}", user.getUsername(),
								ZoneImpl.this.getZoneName());
						zonePlugin.userDisconnect(user, reason);
						Callable callback = userEvent.getCallback();
						if (callback != null) {
							callback.call(user);
						}
					} catch (Exception e) {
						getLogger().error("handle user leave zone get exception", e);
					}
				}
			};
			getScheduledService().execute(callback);
		}
	}

	private String zoneName;
	private RoomManager roomManager;
	private UserManager userManager;
	private SessionManager sessionManager;
	private ZonePlugin zonePlugin;
	private MarioApi marioApi;
	private PluginManager pluginManager;
	private String extensionName;
	private MGSScheduledService scheduledService;
	private Map<String, PuValue> zoneVariables;
	private JoinRoomCallback joinRoomCallback;
	private JoinRoomNavigator joinRoomNavigator;

	private ZoneImpl() {
		roomManager = new RoomManager();
		userManager = new UserManager();
		userManager.addEventListener(UserEvent.ADD, new AddUserEventHandler());
		userManager.addEventListener(UserEvent.REMOVE, new RemoveUserEventHandler());
		zoneVariables = new ConcurrentHashMap<>();
	}

	public ZoneImpl(String zoneName, ZonePlugin zonePlugin, MarioApi marioApi, SessionManager sessionManager) {
		this();
		this.zoneName = zoneName;
		this.zonePlugin = zonePlugin;
		this.marioApi = marioApi;
		this.sessionManager = sessionManager;
	}

	private void sendLoginSuccess(User user) {
		SocketSession socketSession = this.marioApi.getSocketSession(user.getSessionId());
		if (socketSession != null) {
			LoginResponse message = new LoginResponse();
			message.setSuccess(true);
			message.setUsername(user.getUsername());
			message.setZoneName(this.zoneName);
			try {
				socketSession.send(message.serialize());
			} catch (Exception e) {
				getLogger().error("send login success has exception", e);
			}
		} else {
			this.getUserManager().removeUser(user.getUsername(), UserDisconnectReason.UNKNOWN);
		}
	}

	private void sendLogoutSucess(User user, UserDisconnectReason reason) {
		SocketSession socketSession = this.marioApi.getSocketSession(user.getSessionId());
		if (socketSession != null) {
			LogoutResponse response = new LogoutResponse();
			response.setReason(reason);
			response.setSuccess(true);
			try {
				socketSession.send(response.serialize());
			} catch (Exception e) {
				getLogger().error("send logout user {} not success", user.getUsername(), e);
			}
			if (reason != UserDisconnectReason.LOGOUT) {
				try {
					socketSession.close();
				} catch (Exception e) {
					getLogger().error("close socket session get error", e);
				}
			}
		} else {
			getLogger().warn("cannot send logout success to user {}", user.getUsername());
		}
	}

	@SuppressWarnings("unused")
	private void sendLogoutFailure(User user, UserDisconnectReason reason) {
		SocketSession socketSession = this.marioApi.getSocketSession(user.getSessionId());
		if (socketSession != null) {
			LogoutResponse response = new LogoutResponse();
			response.setReason(reason);
			response.setSuccess(false);
			socketSession.send(response.serialize());
		} else {
			getLogger().warn("cannot send logout success to user {}", user.getUsername());
		}
	}

	@Override
	public String getZoneName() {
		return this.zoneName;
	}

	@Override
	public RoomManager getRoomManager() {
		return this.roomManager;
	}

	@Override
	public ZonePlugin getPlugin() {
		return this.zonePlugin;
	}

	public ZonePlugin getZonePlugin() {
		return zonePlugin;
	}

	public void setZonePlugin(ZonePlugin zonePlugin) {
		this.zonePlugin = zonePlugin;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public void setRoomManager(RoomManager roomManager) {
		this.roomManager = roomManager;
	}

	public MarioApi getMarioApi() {
		return marioApi;
	}

	public void setMarioApi(MarioApi marioApi) {
		this.marioApi = marioApi;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	public Room findRoomById(int roomId) {
		return this.roomManager.findRoomById(roomId);
	}

	@Override
	public void stop() {
		if (this.userManager != null) {
			this.userManager.stop();
		}

		if (this.roomManager != null) {
			this.roomManager.stop();
		}

		if (this.scheduledService != null) {
			this.scheduledService.shutdown();
		}

		try {
			this.zonePlugin.destroy();
		} catch (Exception e) {
			System.out.println("destroy zone plugin ex " + e);
		}
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	public String getExtensionName() {
		return extensionName;
	}

	public void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}

	public MGSScheduledService getScheduledService() {
		return scheduledService;
	}

	public void setScheduledService(MGSScheduledService scheduledService) {
		this.scheduledService = scheduledService;
	}

	public Map<String, PuValue> getZoneVariables() {
		return zoneVariables;
	}

	public void setZoneVariables(Map<String, PuValue> zoneVariables) {
		this.zoneVariables = zoneVariables;
	}

	@Override
	public PuValue getZoneVariable(String key) {
		return zoneVariables.get(key);
	}

	public JoinRoomCallback getJoinRoomCallback() {
		return joinRoomCallback;
	}

	public void setJoinRoomCallback(JoinRoomCallback joinRoomCallback) {
		this.joinRoomCallback = joinRoomCallback;
	}

	@Override
	public boolean variableExists(String key) {
		return this.zoneVariables.containsKey(key);
	}

	public JoinRoomNavigator getJoinRoomNavigator() {
		return joinRoomNavigator;
	}

	public void setJoinRoomNavigator(JoinRoomNavigator joinRoomNavigator) {
		this.joinRoomNavigator = joinRoomNavigator;
	}
}
