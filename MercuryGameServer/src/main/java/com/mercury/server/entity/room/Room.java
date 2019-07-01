package com.mercury.server.entity.room;

import java.util.Map;
import java.util.Set;

import com.mercury.server.entity.user.User;
import com.mercury.server.plugin.RoomPlugin;
import com.nhb.common.async.executor.DisruptorAsyncTaskExecutor;
import com.nhb.common.data.PuValue;

public interface Room {

	int getRoomId();

	Set<User> getUsers();

	int getMaxUser();

	boolean hasPassword();

	String getPassword();

	String getRoomName();

	Map<String, PuValue> getRoomVariables();

	PuValue getRoomVariable(String key);
	
	boolean variableExists(String key);

	RoomRemoveMode getRemoveMode();

	User getCreator();

	int size();

	RoomPlugin getRoomPlugin();

	String getGroupId();
	
	boolean isDestroy();
	
	DisruptorAsyncTaskExecutor getExecutor();
}
