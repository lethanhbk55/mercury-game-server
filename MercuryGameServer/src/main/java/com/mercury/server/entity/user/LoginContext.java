package com.mercury.server.entity.user;

import java.util.HashMap;
import java.util.Map;

import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;

public class LoginContext {

	private String username;
	private String password;
	private PuObject params;
	private String newLoginName;
	private Map<String, PuValue> userVariables;
	private String sessionId;

	public LoginContext() {
		userVariables = new HashMap<>();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PuObject getParams() {
		return params;
	}

	public void setParams(PuObject params) {
		this.params = params;
	}

	public String getNewLoginName() {
		return this.newLoginName;
	}

	public void setNewLoginName(String username) {
		this.newLoginName = username;
	}

	public void setUserVariable(String key, PuValue value) {
		userVariables.put(key, value);
	}

	public Map<String, PuValue> getUserVariables() {
		return this.userVariables;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
