package com.mercury.server.test;

import com.mario.config.WorkerPoolConfig;
import com.mario.schedule.ScheduledCallback;
import com.mario.schedule.ScheduledFuture;
import com.mercury.server.extension.config.SchedulerConfig;
import com.mercury.server.schedule.MGSScheduledService;
import com.nhb.common.BaseLoggable;
import com.nhb.common.utils.Initializer;

public class TestScheduledService extends BaseLoggable {
	static {
		Initializer.bootstrap(TestScheduledService.class);
	}

	public static void main(String[] args) {
		TestScheduledService app = new TestScheduledService();
		SchedulerConfig config = new SchedulerConfig();
		config.setCounter(4);
		WorkerPoolConfig workerPoolConfig = new WorkerPoolConfig();
		workerPoolConfig.setPoolSize(8);
		workerPoolConfig.setRingBufferSize(1024);
		workerPoolConfig.setThreadNamePattern("Worker #%d");
		config.setWorkerPoolConfig(workerPoolConfig);

		MGSScheduledService service = new MGSScheduledService(config);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				service.shutdown();
			}
		}));

		ScheduledFuture future = service.schedule(2000, 2000, 10, new ScheduledCallback() {
			private int count = 0;

			@Override
			public void call() {
				app.getLogger().info("run after 2s: {}", ++count);
			}
		});

		service.schedule(1000, 1000, 10, new ScheduledCallback() {

			@Override
			public void call() {
				app.getLogger().info("future remaining time: {}", future.getRemainingTimeToNextOccurrence());
			}
		});

	}
}
