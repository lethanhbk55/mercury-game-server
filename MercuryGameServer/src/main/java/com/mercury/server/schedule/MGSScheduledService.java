package com.mercury.server.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
	public static final Map<Long, ScheduledFuture> FutureIdMapping = new ConcurrentHashMap<>();
	private static final AtomicInteger idSeed = new AtomicInteger();

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
		MGSScheduledFuture scheduledFuture = new MGSScheduledFuture(idSeed.incrementAndGet());
		java.util.concurrent.ScheduledFuture<?> future = this.scheduledService.scheduleAtFixedRate(new Runnable() {
			int count = 0;

			@Override
			public void run() {
				if (count >= times) {
					ScheduledFuture future = FutureIdMapping.remove(scheduledFuture.getId());
					future.cancel();
					return;
				}
				execute(callback);
				count++;
			}
		}, delay, period, TimeUnit.MILLISECONDS);

		scheduledFuture.setFuture(future);
		FutureIdMapping.put(scheduledFuture.getId(), scheduledFuture);
		return scheduledFuture;
	}

	public ScheduledFuture schedule(int delay, int period, ScheduledCallback callback) {
		MGSScheduledFuture scheduledFuture = new MGSScheduledFuture(idSeed.incrementAndGet());
		java.util.concurrent.ScheduledFuture<?> future = this.scheduledService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				execute(callback);
			}
		}, delay, period, TimeUnit.MILLISECONDS);

		scheduledFuture.setFuture(future);
		return scheduledFuture;
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

	public static void removeFuture(long id) {
		FutureIdMapping.remove(id);
	}

	public static int size() {
		return FutureIdMapping.size();
	}
}
