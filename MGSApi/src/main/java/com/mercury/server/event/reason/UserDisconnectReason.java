package com.mercury.server.event.reason;

import java.util.concurrent.atomic.AtomicInteger;

public enum UserDisconnectReason {
	UNKNOWN,
	KICKED,
	LOGOUT,
	KICK_LOGIN_OTHER_DEVICE,
	KICK_IDLE;

	private static AtomicInteger idSeed;

	private static int genId() {
		if (idSeed == null) {
			idSeed = new AtomicInteger();
		}
		return idSeed.incrementAndGet();
	}

	private int id;

	private UserDisconnectReason() {
		this.id = genId();
	}

	public int getId() {
		return this.id;
	}

	public static UserDisconnectReason fromId(int id) {
		for (UserDisconnectReason reason : values()) {
			if (reason.getId() == id) {
				return reason;
			}
		}
		return null;
	}
}
