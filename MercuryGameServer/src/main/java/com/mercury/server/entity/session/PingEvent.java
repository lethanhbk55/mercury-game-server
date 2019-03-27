package com.mercury.server.entity.session;

import java.util.Set;

class PingEvent {

	public static enum PingEventType {
		UPDATE,
		REMOVE;
	}

	private String sessionId;
	private PingEventType type;
	private Set<String> sessionsToRemove;
	
	public PingEvent() {
		
	}

	public PingEvent(PingEventType type, String sessionId) {
		this.setType(type);
		this.sessionId = sessionId;
	}

	public PingEvent(PingEventType type, Set<String> sessionsToRemove) {
		setType(type);
		this.setSessionsToRemove(sessionsToRemove);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public PingEventType getType() {
		return type;
	}

	public void setType(PingEventType type) {
		this.type = type;
	}

	public Set<String> getSessionsToRemove() {
		return sessionsToRemove;
	}

	public void setSessionsToRemove(Set<String> sessionsToRemove) {
		this.sessionsToRemove = sessionsToRemove;
	}

}
