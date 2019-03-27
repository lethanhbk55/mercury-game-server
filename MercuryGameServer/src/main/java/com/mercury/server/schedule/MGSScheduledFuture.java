package com.mercury.server.schedule;

import java.util.concurrent.TimeUnit;

import com.mario.schedule.ScheduledFuture;

public class MGSScheduledFuture implements ScheduledFuture {
	private long id;
	private java.util.concurrent.ScheduledFuture<?> future;
	private long startTime;
	private boolean cancelled = false;
	private MGSScheduledService service;

	public MGSScheduledFuture(long id, MGSScheduledService service) {
		autoStartTime();
		this.id = id;
		this.service = service;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void cancel() {
		this.cancelled = true;
		this.service.removeFuture(this.id);
		if (this.future != null) {
			future.cancel(false);
			future = null;
		}
	}

	@Override
	public long getRemainingTimeToNextOccurrence() {
		if (this.future != null) {
			return future.getDelay(TimeUnit.MILLISECONDS);
		}
		return 0;
	}

	@Override
	public long getStartTime() {
		return this.startTime;
	}

	public void autoStartTime() {
		this.startTime = System.currentTimeMillis();
	}

	public void setFuture(java.util.concurrent.ScheduledFuture<?> future) {
		this.future = future;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}
}