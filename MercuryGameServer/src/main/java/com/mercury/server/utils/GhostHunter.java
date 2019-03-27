package com.mercury.server.utils;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.mario.api.MarioApi;
import com.mario.gateway.socket.SocketSession;
import com.mercury.server.entity.session.PingSessionManager;
import com.mercury.server.entity.session.SessionManager;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.nhb.common.BaseLoggable;

public class GhostHunter extends BaseLoggable {
	private ScheduledExecutorService service;
	private PingSessionManager pingSessionManager;
	private SessionManager sessionManager;
	private ZoneManager zoneManager;
	private MarioApi api;
	private int timeForPing;

	public GhostHunter(PingSessionManager pingSessionManager, SessionManager sessionManager, ZoneManager zoneMaanger,
			MarioApi api, int timeForPing) {
		this.pingSessionManager = pingSessionManager;
		this.sessionManager = sessionManager;
		this.api = api;
		this.zoneManager = zoneMaanger;
		service = Executors.newScheduledThreadPool(1, new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				return new Thread(r, "Ghost Hunter");
			}
		});
		this.timeForPing = timeForPing;
	}

	public void start() {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				killSessionTimeout();
			}
		}, timeForPing, timeForPing, TimeUnit.SECONDS);
	}

	private void killSessionTimeout() {
		Set<String> sessionToKill = pingSessionManager.getSessionTimeout(timeForPing * 1000);

		for (String sessionId : sessionToKill) {
			Zone zone = sessionManager.getZoneBySessionId(sessionId);
			if (zone != null) {
				boolean userExists = zone.getUserManager().removeUserBySessionId(sessionId,
						UserDisconnectReason.KICK_IDLE);
				if (!userExists) {
					sessionManager.removeSession(sessionId);
				}
			} else {
				SocketSession socketSession = this.api.getSocketSession(sessionId);
				if (socketSession != null) {
					try {
						socketSession.close();
					} catch (Exception e) {
						getLogger().error("close idle session {} get error, e", sessionId);
					}
				}
				sessionManager.removeSession(sessionId);
			}

			pingSessionManager.removeSession(sessionId);
		}

		Collection<Zone> zones = zoneManager.getZones();
		for (Zone zone : zones) {
			Collection<User> users = zone.getUserManager().getUsers();
			for (User user : users) {
				SocketSession socketSession = this.api.getSocketSession(user.getSessionId());
				if (socketSession == null) {
					zone.getUserManager().removeUser(user.getUsername(), UserDisconnectReason.UNKNOWN);
				}
			}
		}
	}

	public void stop() {
		this.service.shutdown();
		try {
			if (this.service.awaitTermination(3, TimeUnit.SECONDS)) {
				this.service.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
