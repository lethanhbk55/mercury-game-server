package com.mercury.server.entity.zone;

import java.util.Map;

import com.mario.api.MarioApi;
import com.mercury.server.callback.JoinRoomCallback;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.room.RoomManager;
import com.mercury.server.entity.user.UserManager;
import com.mercury.server.navigator.JoinRoomNavigator;
import com.mercury.server.plugin.PluginManager;
import com.mercury.server.plugin.ZonePlugin;
import com.nhb.common.data.PuValue;

public interface Zone {

	String getZoneName();

	String getExtensionName();

	RoomManager getRoomManager();

	UserManager getUserManager();

	ZonePlugin getPlugin();

	PluginManager getPluginManager();

	MarioApi getMarioApi();

	Room findRoomById(int roomId);

	void stop();

	Map<String, PuValue> getZoneVariables();

	PuValue getZoneVariable(String key);

	JoinRoomCallback getJoinRoomCallback();

	JoinRoomNavigator getJoinRoomNavigator();

	boolean variableExists(String key);
}
