package com.mercury.server.event.reason;

import java.util.concurrent.atomic.AtomicInteger;

public enum UserLeaveRoomReason {
	LEAVE_ROOM,
	KICKED;

	private static AtomicInteger idSeed;

	private static int genId() {
		if (idSeed == null) {
			idSeed = new AtomicInteger();
		}
		return idSeed.incrementAndGet();
	}

	private int id;

	private UserLeaveRoomReason() {
		this.id = genId();
	}

	public int getId() {
		return this.id;
	}

	public static UserLeaveRoomReason fromId(int id) {
		for (UserLeaveRoomReason reason : values()) {
			if (reason.getId() == id) {
				return reason;
			}
		}
		return null;
	}
}
