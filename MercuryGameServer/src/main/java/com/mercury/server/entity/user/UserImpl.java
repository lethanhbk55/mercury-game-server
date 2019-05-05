package com.mercury.server.entity.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mercury.server.entity.room.Room;
import com.nhb.common.data.PuValue;

public class UserImpl implements User {

	private String username;
	private String sessionId;
	private Map<String, PuValue> userVariables;
	private String ip;
	private Room lastJoinedRoom;
	private long loginTime;

	public UserImpl() {
		userVariables = new ConcurrentHashMap<>();
	}

	public UserImpl(String username, String sessionId) {
		this();
		setUsername(username);
		setSessionId(sessionId);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Map<String, PuValue> getUserVariables() {
		return userVariables;
	}

	public void setUserVariables(Map<String, PuValue> userVariables) {
		this.userVariables = userVariables;
	}

	@Override
	public PuValue getUserVariable(String key) {
		return this.userVariables.get(key);
	}

	@Override
	public void setUserVariable(String key, PuValue value) {
		this.userVariables.put(key, value);
	}

	@Override
	public String getIP() {
		return this.ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public Room getLastJoinedRoom() {
		return lastJoinedRoom;
	}

	public void setLastJoinedRoom(Room lastJoinedRoom) {
		this.lastJoinedRoom = lastJoinedRoom;
	}

	@Override
	public boolean containsVariable(String key) {
		return this.userVariables.containsKey(key);
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

}
