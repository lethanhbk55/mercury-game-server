package com.mercury.server.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.mario.config.WorkerPoolConfig;
import com.mario.schedule.ScheduledCallback;
import com.mario.schedule.ScheduledFuture;
import com.mercury.server.extension.config.SchedulerConfig;
import com.nhb.common.BaseLoggable;
import com.nhb.common.async.executor.DisruptorAsyncTaskExecutor;

import lombok.Setter;

@Setter
public class MGSScheduledService extends BaseLoggable {
	private ScheduledExecutorService scheduledService;
	private DisruptorAsyncTaskExecutor executor;

	public MGSScheduledService(ScheduledExecutorService scheduledService, DisruptorAsyncTaskExecutor executor) {
		this.scheduledService = scheduledService;
		this.executor = executor;
	}

	public MGSScheduledService(SchedulerConfig schedulerConfig) {
		WorkerPoolConfig workerPoolConfig = schedulerConfig.getWorkerPoolConfig();
		this.setExecutor(DisruptorAsyncTaskExecutor.createMultiProducerExecutor(workerPoolConfig.getPoolSize(),
				workerPoolConfig.getRingBufferSize(), workerPoolConfig.getThreadNamePattern()));
		this.setScheduledService(Executors.newScheduledThreadPool(schedulerConfig.getCounter(), new ThreadFactory() {
			private AtomicInteger idSeed = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable runnale) {
				return new Thread(runnale, String.format("MGS Scheduler #%d", idSeed.getAndIncrement()));
			}
		}));

		this.executor.start();
	}

	public void execute(ScheduledCallback callback) {
		this.executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					callback.call();
				} catch (Exception e) {
					getLogger().error("execute callback has exception", e);
				}
			}
		});
	}

	public ScheduledFuture schedule(int delay, int period, int times, ScheduledCallback callback) {
		MGSScheduledFuture result = new MGSScheduledFuture(delay);
		result.setFuture(this.scheduledService.scheduleAtFixedRate(new Runnable() {

			private AtomicInteger count = new AtomicInteger();;

			@Override
			public void run() {
				if (result.isCancelled()) {
					return;
				}

				execute(callback);
				result.setDelay(delay);
				result.updateStartTime();

				if (count.incrementAndGet() == times) {
					result.cancel();
				}
			}
		}, delay, period, TimeUnit.MILLISECONDS));
		result.updateStartTime();
		return result;
	}

	public ScheduledFuture schedule(int delay, int period, ScheduledCallback callback) {
		MGSScheduledFuture result = new MGSScheduledFuture(delay);
		result.setFuture(this.scheduledService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (result.isCancelled()) {
					return;
				}

				execute(callback);
				result.setDelay(delay);
				result.updateStartTime();
			}
		}, delay, period, TimeUnit.MILLISECONDS));
		result.updateStartTime();
		return result;
	}

	public void shutdown() {
		if (scheduledService != null) {
			scheduledService.shutdown();
			try {
				if (scheduledService.awaitTermination(3, TimeUnit.SECONDS)) {
					scheduledService.shutdownNow();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (this.executor != null) {
			try {
				this.executor.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
