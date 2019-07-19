package com.mercury.server.schedule;

import java.util.concurrent.TimeUnit;

import com.mario.schedule.ScheduledFuture;
import com.nhb.common.BaseLoggable;

public class MGSScheduledFuture extends BaseLoggable implements ScheduledFuture {
	private long id;
	private java.util.concurrent.ScheduledFuture<?> future;
	private long startTime;
	private boolean cancelled = false;

	public MGSScheduledFuture(long id) {
		autoStartTime();
		this.id = id;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public void cancel() {
		this.cancelled = true;
		MGSScheduledService.FutureIdMapping.remove(this.getId());
		if (this.future != null) {
			getLogger().debug("[schedule] cancel schedule {}", getId());
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
