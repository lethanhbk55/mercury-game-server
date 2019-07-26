package com.mercury.server.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mario.gateway.socket.SocketSession;
import com.mario.schedule.ScheduledCallback;
import com.mario.schedule.ScheduledFuture;
import com.mercury.server.api.setting.CreateRoomSetting;
import com.mercury.server.entity.room.AbstractRoom;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.user.UserManager;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.data.ErrorCode;
import com.mercury.server.extension.loader.ExtensionManager;
import com.mercury.server.plugin.RoomPlugin;
import com.mercury.server.response.impl.ExtensionResponse;
import com.mercury.server.schedule.MGSScheduledService;
import com.nhb.common.BaseLoggable;
import com.nhb.common.async.executor.DisruptorAsyncTaskExecutor;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;

class PluginApiImpl extends BaseLoggable implements PluginApi {
	private ExtensionManager extensionManager;
	private Zone zone;
	private Room room;
	private String extensionName;
	private PluginApiFactory factory;
	private ZoneManager zoneManager;

	public PluginApiImpl(ExtensionManager extensionManager, Zone zone, PluginApiFactory factory,
			ZoneManager zoneManager) {
		this.extensionManager = extensionManager;
		this.zone = zone;
		this.factory = factory;
		this.zoneManager = zoneManager;
		this.extensionName = zone.getExtensionName();
	}

	@Override
	public void createRoom(CreateRoomSetting setting) {
		RoomPlugin roomPlugin = extensionManager.newInstance(getExtensionName(), setting.getPluginClass());
		roomPlugin.setMarioApi(this.factory.newMarioApi());

		RoomImpl room = new RoomImpl();
		room.setRoomName(setting.getRoomName());
		room.setRoomPlugin(roomPlugin);
		room.setHasPassword(setting.isHasPassword());
		room.setPassword(setting.getPassword());
		room.setMaxUser(setting.getMaxUser());
		room.setRemoveMode(setting.getRemoveMode());
		room.setCreator(setting.getCreator());
		room.setGroupId(setting.getGroupId());
		room.setRoomVariables(setting.getRoomVariables());
		room.setMessenger(new RoomMessengerImpl(factory.newMarioApi()));
		roomPlugin.setPluginApi(this.factory.newRoomPluginApi(this.getZoneName(), room));
		zone.getRoomManager().addRoom(room, setting, zone.getRoomExecutor());
	}

	public void removeRoom(int roomId) {
		zone.getRoomManager().removeRoom(roomId);
	}

	@Override
	public void callRoomPlugin(int roomId, PuObject params) {
		Room room = this.zone.findRoomById(roomId);
		if (room != null) {
			room.getRoomPlugin().interop(params);
		}
	}

	public Object aquireRoomPlugin(int roomId, PuObject params) {
		Room room = this.zone.findRoomById(roomId);
		if (room != null) {
			return room.getRoomPlugin().aquireObject(params);
		}
		return null;
	}

	@Override
	public void requestRoomPlugin(int roomId, User user, PuObject params) {
		Room room = this.zone.findRoomById(roomId);
		if (room != null) {
			room.getExecutor().execute(new Runnable() {

				@Override
				public void run() {
					try {
						room.getRoomPlugin().request(user, params);
					} catch (Exception e) {
						getLogger().error("request rooom plugin has exception", e);
					}
				}
			});
		}
	}

	@Override
	public void callPlugin(String name, PuObject params) {
		this.zone.getPluginManager().interop(name, params);
	}

	public void requestPlugin(String name, User user, PuObject params) {
		this.zone.getPluginManager().request(name, user, params);
	}

	private boolean sendMessageToUser(User user, PuObject message) {
		String sessionId = user.getSessionId();
		SocketSession socketSession = this.factory.newMarioApi().getSocketSession(sessionId);
		if (socketSession != null) {
			try {
				socketSession.send(new ExtensionResponse(message).serialize());
				return true;
			} catch (Exception e) {
				getLogger().warn("Cannot send message to user {}", user.getUsername(), e);
			}
		} else {
			getLogger().warn("Cannot send message to user {} cause socket session is null", user.getUsername());
		}
		return false;
	}

	@Override
	public boolean sendMessageToUser(String username, PuObject message) {
		UserManager userManager = this.zone.getUserManager();
		User user = userManager.getByUsername(username);
		if (user != null) {
			return sendMessageToUser(user, message);
		}
		return false;
	}

	@Override
	public void sendMessageToRoom(int roomId, PuObject message) {
		Room room = this.zone.getRoomManager().findRoomById(roomId);
		if (room != null) {
			Set<User> users = room.getUsers();
			for (User user : users) {
				this.sendMessageToUser(user, message);
			}
		}
	}

	@Override
	public void sendMessageToZone(PuObject message) {
		Collection<User> users = this.zone.getUserManager().getUsers();
		for (User user : users) {
			this.sendMessageToUser(user, message);
		}
	}

	@Override
	public String getExtensionName() {
		return this.extensionName;
	}

	public int getRoomId() {
		if (this.room != null) {
			return this.room.getRoomId();
		}
		return -1;
	}

	public String getRoomName() {
		if (this.room != null) {
			return this.room.getRoomName();
		}
		return null;
	}

	public String getZoneName() {
		return this.zone.getZoneName();
	}

	@Override
	public boolean kickUserFromRoom(String username, int roomId, ErrorCode code) {
		Room room = this.zone.findRoomById(roomId);
		User user = this.zone.getUserManager().getByUsername(username);
		if (room != null && user != null) {
			try {
				((AbstractRoom) room).leaveRoom(user, UserLeaveRoomReason.KICKED, code);
			} catch (Exception e) {
				getLogger().debug("kick user {} get error", username, e);
			}
			return true;
		}
		return false;
	}

	void setRoom(Room room) {
		this.room = room;
	}

	@Override
	public void callCrossZone(String zoneName, PuObject params) {
		Zone zone = zoneManager.getZone(zoneName);
		if (zone != null) {
			zone.getPlugin().interop(params);
		}
	}

	@Override
	public ScheduledFuture schedule(int delay, int times, ScheduledCallback callback) {
		DisruptorAsyncTaskExecutor executor;
		if (this.room != null) {
			executor = room.getExecutor();
		} else {
			executor = this.zone.getExecutor();
		}
		return new MGSScheduledService(this.zone.getScheduledService(), executor).schedule(delay, delay, times,
				callback);
	}

	@Override
	public ScheduledFuture schedule(int delay, int pediod, int times, ScheduledCallback callback) {
		DisruptorAsyncTaskExecutor executor;
		if (this.room != null) {
			executor = room.getExecutor();
		} else {
			executor = this.zone.getExecutor();
		}
		return new MGSScheduledService(this.zone.getScheduledService(), executor).schedule(delay, pediod, times,
				callback);
	}

	@Override
	public void execute(ScheduledCallback callback) {
		DisruptorAsyncTaskExecutor executor;
		if (this.room != null) {
			executor = room.getExecutor();
		} else {
			executor = this.zone.getExecutor();
		}
		new MGSScheduledService(this.zone.getScheduledService(), executor).execute(callback);
	}

	@Override
	public Set<String> getGroups() {
		return this.zone.getRoomManager().getGroups();
	}

	@Override
	public List<Room> getRoomsByGroupId(String groupId) {
		return this.zone.getRoomManager().getRoomsByGroupId(groupId);
	}

	@Override
	public PuValue getRoomVariable(String key) {
		if (room != null && room.getRoomVariables() != null) {
			return room.getRoomVariable(key);
		}
		return null;
	}

	@Override
	public void sendMessageToRoom(int roomId, PuObject message, List<String> usernames) {
		Room room = this.zone.getRoomManager().findRoomById(roomId);
		if (room != null) {
			Collection<User> users = room.getUsers();
			for (User user : users) {
				if (!usernames.contains(user.getUsername())) {
					this.sendMessageToUser(user, message);
				}
			}
		}
	}

	@Override
	public void sendMessageToRoom(int roomId, PuObject message, String... exceptedUsers) {
		List<String> usernames = Arrays.asList(exceptedUsers);
		this.sendMessageToRoom(roomId, message, usernames);
	}

	@Override
	public Object aquireObject(String pluginName, PuObject params) {
		return zone.getPluginManager().aquire(pluginName, params);
	}

	@Override
	public void releaseObject(String pluginName, PuObject params) {
		zone.getPluginManager().release(pluginName, params);
	}

	@Override
	public User getUserByUsername(String username) {
		return zone.getUserManager().getByUsername(username);
	}

	@Override
	public PuValue getZoneVariable(String key) {
		return zone.getZoneVariable(key);
	}

	@Override
	public void setZoneVariable(String key, PuValue value) {
		zone.getZoneVariables().put(key, value);
	}

	public boolean zoneVariableExists(String key) {
		return zone.variableExists(key);
	}

	@Override
	public Object aquireObject(int roomId, PuObject params) {
		Room room = zone.findRoomById(roomId);
		if (room != null) {
			return room.getRoomPlugin().aquireObject(params);
		}
		return null;
	}

	@Override
	public void releaseObject(int roomId, PuObject params) {
		Room room = zone.findRoomById(roomId);
		if (room != null) {
			room.getRoomPlugin().releaseObject(params);
		}
	}

	@Override
	public void kickUserOnServer(String username, UserDisconnectReason reason) {
		this.zone.getUserManager().removeUser(username, reason);
	}

	@Override
	public Room findByRoomId(int roomId) {
		return this.zone.getRoomManager().findRoomById(roomId);
	}

	@Override
	public boolean roomVariableExists(String key) {
		if (this.room != null) {
			Map<String, PuValue> roomVariables = this.room.getRoomVariables();
			if (roomVariables != null) {
				return roomVariables.containsKey(key);
			}
		}
		return false;
	}

	@Override
	public Object aquireCrossZone(String zoneName, PuObject params) {
		Zone zone = zoneManager.getZone(zoneName);
		if (zone != null) {
			return zone.getPlugin().aquireObject(params);
		}
		return null;
	}

	@Override
	public void releaseCrossZone(String zoneName, PuObject params) {
		Zone zone = zoneManager.getZone(zoneName);
		if (zone != null) {
			zone.getPlugin().releaseObject(params);
		}
	}

	@Override
	public String getRoomPassword() {
		if (this.room != null) {
			return this.room.getPassword();
		}
		return "";
	}

	@Override
	public void setRoomPassword(String password) {
		if (this.room instanceof RoomImpl) {
			((RoomImpl) this.room).setPassword(password);
			((RoomImpl) room).setHasPassword(password == null || !password.isEmpty());
		}
	}

	@Override
	public void setRoomPassword(int roomId, String password) {
		Room room = findByRoomId(roomId);
		if (room != null && room instanceof RoomImpl) {
			((RoomImpl) room).setPassword(password);
			((RoomImpl) room).setHasPassword(password == null || !password.isEmpty());
		}
	}

	public boolean checkRoomHasPassword() {
		if (this.room != null) {
			return this.room.hasPassword();
		}
		return false;
	}
}
