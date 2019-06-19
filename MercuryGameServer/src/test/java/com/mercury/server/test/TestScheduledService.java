package com.mercury.server.test;

import com.mario.config.WorkerPoolConfig;
import com.mario.schedule.ScheduledCallback;
import com.mario.schedule.ScheduledFuture;
import com.mercury.server.extension.config.SchedulerConfig;
import com.mercury.server.schedule.MGSScheduledService;

public class TestScheduledService {
	private static ScheduledFuture future;

	public static void main(String[] args) {
		SchedulerConfig config = new SchedulerConfig();
		config.setCounter(4);
		WorkerPoolConfig workerPoolConfig = new WorkerPoolConfig();
		workerPoolConfig.setPoolSize(8);
		workerPoolConfig.setRingBufferSize(1024);
		config.setWorkerPoolConfig(workerPoolConfig);
		
		MGSScheduledService service = new MGSScheduledService(config);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				service.shutdown();
			}
		}));
		future = service.schedule(2000, 2000, 10, new ScheduledCallback() {

			@Override
			public void call() {
				System.out.println("Betting....");
			}
		});
		
		

		service.schedule(3000, 3000, 1, new ScheduledCallback() {
			
			@Override
			public void call() {
				future.cancel();
				System.out.println("size " + MGSScheduledService.size());
			}
		});
		
		
	}
}
