package com.mercury.server.entity.user;

import java.util.Map;

import com.mercury.server.entity.room.Room;
import com.nhb.common.data.PuValue;

public interface User {
	
	String getUsername();

	String getSessionId();
	
	String getIP();
	
	boolean containsVariable(String key);

	PuValue getUserVariable(String key);

	void setUserVariable(String key, PuValue value);
	
	Map<String, PuValue> getUserVariables();

	void setUserVariables(Map<String, PuValue> userVariables);
	
	Room getLastJoinedRoom();
	
	long getLoginTime();
}
