package com.mercury.server.utils;

import com.mario.api.MarioApi;
import com.mario.schedule.ScheduledCallback;
import com.mercury.server.entity.session.SessionManager;
import com.mercury.server.entity.zone.ZoneManager;
import com.nhb.common.BaseLoggable;

public final class CCULog extends BaseLoggable {

	private static final int PERIOD = 60000;

	private MarioApi api;
	private ZoneManager zoneManager;
	private SessionManager sessionManager;

	public CCULog(MarioApi api, ZoneManager zoneManager, SessionManager sessionManager) {
		this.api = api;
		this.zoneManager = zoneManager;
		this.sessionManager = sessionManager;
	}

	public void start() {
		api.getScheduler().scheduleAtFixedRate(PERIOD, PERIOD, new ScheduledCallback() {

			@Override
			public void call() {
				int userCount = zoneManager.getUserCount();
				int roomCount = zoneManager.getRoomCount();
				getLogger().info("======>CCUs: {}, Sessions: {}, Room: {}", userCount, sessionManager.size(),
						roomCount);
			}
		});
	}
}
