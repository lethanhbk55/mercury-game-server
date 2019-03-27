package com.mercury.server.schedule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WorkHandler;
import com.mario.config.WorkerPoolConfig;
import com.mario.schedule.ScheduledCallback;
import com.mario.schedule.ScheduledFuture;
import com.mercury.server.extension.config.SchedulerConfig;
import com.nhb.common.BaseLoggable;

public class MGSScheduledService extends BaseLoggable implements ExceptionHandler<MGSEvent> {
	class MGSWokerHandler implements WorkHandler<MGSEvent> {

		@Override
		public void onEvent(MGSEvent event) throws Exception {
			event.getCallback().call();
		}

	}

	private ScheduledExecutorService scheduledService;
	private static AtomicInteger idSeed = new AtomicInteger();
	private Map<Long, ScheduledFuture> futureIdMaping = new ConcurrentHashMap<>();
	private ExecutorService executorService;
	// private WorkerPool<MGSEvent> workerPool;
	// private RingBuffer<MGSEvent> ringBuffer;

	public MGSScheduledService(SchedulerConfig config) {
		WorkerPoolConfig workerPoolConfig = config.getWorkerPoolConfig();
		initWorkerPool(workerPoolConfig.getRingBufferSize(), workerPoolConfig.getPoolSize(),
				workerPoolConfig.getThreadNamePattern());
		initCounters(config.getCounter());
	}

	private void initWorkerPool(int ringBufferSize, int numWorkers, String threadNameParttern) {
		MGSWokerHandler[] workers = new MGSWokerHandler[numWorkers];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new MGSWokerHandler();
		}

		int corePoolSize = numWorkers / 2;

		this.executorService = new ThreadPoolExecutor(corePoolSize, numWorkers, 6l, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat(threadNameParttern).build());

		// this.workerPool = new WorkerPool<MGSEvent>(new
		// EventFactory<MGSEvent>() {
		//
		// @Override
		// public MGSEvent newInstance() {
		// return new MGSEvent();
		// }
		// }, this, workers);
		//
		// this.ringBuffer = workerPool.start(new ThreadPoolExecutor(numWorkers,
		// numWorkers, 6l, TimeUnit.SECONDS,
		// new LinkedBlockingDeque<Runnable>(),
		// new
		// ThreadFactoryBuilder().setNameFormat(threadNameParttern).build()));
	}

	private void initCounters(int counter) {
		scheduledService = Executors.newScheduledThreadPool(counter, new ThreadFactory() {
			private AtomicInteger idSeed = new AtomicInteger(1);

			@Override
			public Thread newThread(Runnable runnale) {
				return new Thread(runnale, String.format("MGS Scheduler #%d", idSeed.getAndIncrement()));
			}
		});
	}

	public void execute(ScheduledCallback callback) {
		// if (callback == null) {
		// return;
		// }
		// long sequence = this.ringBuffer.next();
		// try {
		// MGSEvent event = this.ringBuffer.get(sequence);
		// event.setCallback(callback);
		// } finally {
		// this.ringBuffer.publish(sequence);
		// }
		this.executorService.execute(new Runnable() {

			@Override
			public void run() {
				callback.call();
			}
		});
	}

	public ScheduledFuture schedule(int delay, int period, int times, ScheduledCallback callback) {
		MGSScheduledFuture scheduledFuture = new MGSScheduledFuture(idSeed.incrementAndGet(), this);
		java.util.concurrent.ScheduledFuture<?> future = this.scheduledService.scheduleAtFixedRate(new Runnable() {
			int count = 0;

			@Override
			public void run() {
				if (count >= times) {
					ScheduledFuture future = futureIdMaping.remove(scheduledFuture.getId());
					future.cancel();
					System.out.println(futureIdMaping.size());
					return;
				}
				execute(callback);
				count++;
			}
		}, delay, period, TimeUnit.MILLISECONDS);

		scheduledFuture.setFuture(future);
		futureIdMaping.put(scheduledFuture.getId(), scheduledFuture);
		return scheduledFuture;
	}

	public ScheduledFuture schedule(int delay, int period, ScheduledCallback callback) {
		MGSScheduledFuture scheduledFuture = new MGSScheduledFuture(idSeed.incrementAndGet(), this);
		java.util.concurrent.ScheduledFuture<?> future = this.scheduledService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				execute(callback);
			}
		}, delay, period, TimeUnit.MILLISECONDS);

		scheduledFuture.setFuture(future);
		futureIdMaping.put(scheduledFuture.getId(), scheduledFuture);
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

		// if (this.workerPool != null && this.workerPool.isRunning()) {
		// try {
		// this.workerPool.drainAndHalt();
		// } catch (Exception ex) {
		// System.err.println("stop schedule executor error");
		// ex.printStackTrace();
		// }
		// }

		if (this.executorService != null) {
			this.executorService.shutdown();
			try {
				if (this.executorService.awaitTermination(3, TimeUnit.SECONDS)) {
					this.executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handleEventException(Throwable arg0, long arg1, MGSEvent arg2) {

	}

	@Override
	public void handleOnShutdownException(Throwable ex) {

	}

	@Override
	public void handleOnStartException(Throwable ex) {

	}

	public int size() {
		return this.futureIdMaping.size();
	}

	public void removeFuture(long id) {
		this.futureIdMaping.remove(id);
	}
}
