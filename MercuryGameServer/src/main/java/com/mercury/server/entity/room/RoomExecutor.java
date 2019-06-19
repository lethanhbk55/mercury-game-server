package com.mercury.server.entity.room;

import java.util.ArrayList;
import java.util.List;

import com.mario.config.WorkerPoolConfig;
import com.mercury.server.extension.config.RoomConfig;
import com.nhb.common.async.executor.DisruptorAsyncTaskExecutor;

public class RoomExecutor {

	private List<DisruptorAsyncTaskExecutor> executors = new ArrayList<>();

	public RoomExecutor(RoomConfig roomConfig) {
		WorkerPoolConfig workerPoolConfig = roomConfig.getWorkerPoolConfig();
		for (int i = 0; i < workerPoolConfig.getPoolSize(); i++) {
			this.executors
					.add(DisruptorAsyncTaskExecutor.createSingleProducerExecutor(workerPoolConfig.getRingBufferSize(),
							1, String.format(workerPoolConfig.getThreadNamePattern(), i)));
		}
	}

	public void start() {
		for (DisruptorAsyncTaskExecutor executor : this.executors) {
			executor.start();
		}
	}

	public void execute(int roomId, Runnable runnable) {
		DisruptorAsyncTaskExecutor executor = getExecutor(roomId);
		if (executor != null) {
			executor.execute(runnable);
		} else {
			runnable.run();
		}
	}

	public DisruptorAsyncTaskExecutor getExecutor(int roomId) {
		int executorId = roomId % this.executors.size();
		return this.executors.get(executorId);
	}

	public void shutdown() {
		for (DisruptorAsyncTaskExecutor executor : this.executors) {
			try {
				executor.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
