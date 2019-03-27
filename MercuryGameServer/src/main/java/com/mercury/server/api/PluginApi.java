package com.mercury.server.api;

import java.util.List;
import java.util.Set;

import com.mario.schedule.ScheduledCallback;
import com.mario.schedule.ScheduledFuture;
import com.mercury.server.api.setting.CreateRoomSetting;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.data.ErrorCode;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;

public interface PluginApi {

	void createRoom(CreateRoomSetting setting);

	void removeRoom(int roomId);

	void callRoomPlugin(int roomId, PuObject params);

	void requestRoomPlugin(int roomId, User user, PuObject params);

	Object aquireRoomPlugin(int roomId, PuObject params);

	void callPlugin(String name, PuObject params);

	void requestPlugin(String name, User user, PuObject params);

	void callCrossZone(String zoneName, PuObject params);

	Object aquireCrossZone(String zoneName, PuObject params);

	void releaseCrossZone(String zoneName, PuObject params);

	boolean sendMessageToUser(String username, PuObject message);

	void sendMessageToRoom(int roomId, PuObject message);

	void sendMessageToRoom(int roomId, PuObject message, String... exceptedUsers);

	void sendMessageToRoom(int roomId, PuObject message, List<String> exceptedUsers);

	void sendMessageToZone(PuObject message);

	String getExtensionName();

	boolean kickUserFromRoom(String username, int roomId, ErrorCode code);

	int getRoomId();

	String getZoneName();

	ScheduledFuture schedule(int delay, int times, ScheduledCallback callback);

	ScheduledFuture schedule(int delay, int period, int times, ScheduledCallback callback);

	void execute(ScheduledCallback callback);

	Set<String> getGroups();

	List<Room> getRoomsByGroupId(String groupId);

	PuValue getRoomVariable(String key);

	boolean roomVariableExists(String key);

	String getRoomName();

	Object aquireObject(String pluginName, PuObject params);

	Object aquireObject(int roomId, PuObject params);

	void releaseObject(String pluginName, PuObject params);

	void releaseObject(int roomId, PuObject params);

	User getUserByUsername(String username);

	PuValue getZoneVariable(String key);

	void setZoneVariable(String key, PuValue value);

	boolean zoneVariableExists(String key);

	void kickUserOnServer(String username, UserDisconnectReason reason);

	Room findByRoomId(int roomId);

	void setRoomPassword(String password);
	
	String getRoomPassword();
	
	boolean checkRoomHasPassword();
}
