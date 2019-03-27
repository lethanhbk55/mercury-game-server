package com.mercury.server.entity.session;

import com.mercury.server.entity.zone.Zone;

class SessionEvent {

	static enum SessionEventType {
		ADD,
		REMOVE;
	}

	private SessionEventType eventType;
	private String sessionId;
	private Zone zone;

	public SessionEventType getEventType() {
		return eventType;
	}

	public void setEventType(SessionEventType eventType) {
		this.eventType = eventType;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

}
