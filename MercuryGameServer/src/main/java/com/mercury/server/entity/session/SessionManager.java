package com.mercury.server.entity.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mercury.server.entity.zone.Zone;

public class SessionManager {

	private Map<String, Zone> sessionIdZoneMaping;

	public SessionManager() {
		sessionIdZoneMaping = new ConcurrentHashMap<>();
	}

	public void addSessionToZone(String sessionId, Zone zone) {
		sessionIdZoneMaping.put(sessionId, zone);
	}

	public void removeSession(String sessionId) {
		sessionIdZoneMaping.remove(sessionId);
	}

	public Zone getZoneBySessionId(String sessionId) {
		return this.sessionIdZoneMaping.get(sessionId);
	}

	public void shutdown() {

	}

	public int size() {
		return this.sessionIdZoneMaping.size();
	}
}
