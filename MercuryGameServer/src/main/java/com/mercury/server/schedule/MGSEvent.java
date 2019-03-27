package com.mercury.server.schedule;

import com.mario.schedule.ScheduledCallback;

public class MGSEvent {

	private ScheduledCallback callback;

	public MGSEvent() {

	}

	public MGSEvent(ScheduledCallback callback) {
		setCallback(callback);
	}

	public ScheduledCallback getCallback() {
		return callback;
	}

	public void setCallback(ScheduledCallback callback) {
		this.callback = callback;
	}
}
