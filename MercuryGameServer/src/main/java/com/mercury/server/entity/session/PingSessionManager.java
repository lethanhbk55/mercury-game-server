package com.mercury.server.entity.session;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PingSessionManager {

	private Map<String, Long> sessionIdPingTimeMapping;

	public PingSessionManager() {
		sessionIdPingTimeMapping = new ConcurrentHashMap<>();
	}

	public void updatePing(String sessionId) {
		sessionIdPingTimeMapping.put(sessionId, System.currentTimeMillis());
	}

	public Set<String> getSessionTimeout(int idleTime) {
		Set<String> sessionToKill = new HashSet<>();
		long currentTime = System.currentTimeMillis();
		for (Entry<String, Long> entry : sessionIdPingTimeMapping.entrySet()) {
			if (currentTime - entry.getValue() > idleTime) {
				sessionToKill.add(entry.getKey());
			}
		}
		return sessionToKill;
	}

	public void removeSessions(Set<String> sessions) {
		for (String sessionId : sessions) {
			sessionIdPingTimeMapping.remove(sessionId);
		}
	}

	public void removeSession(String sessionId) {
		this.sessionIdPingTimeMapping.remove(sessionId);
	}

	public void shutdown() {

	}
}
