package com.mercury.server.entity.room;

import java.util.HashMap;
import java.util.Map;

import com.mario.config.WorkerPoolConfig;
import com.mercury.server.extension.config.RoomConfig;
import com.nhb.common.async.executor.DisruptorAsyncTaskExecutor;

public class RoomExecutor {

	private Map<Integer, DisruptorAsyncTaskExecutor> executors = new HashMap<>();
	private int size;

	public RoomExecutor(RoomConfig roomConfig) {
		WorkerPoolConfig workerPoolConfig = roomConfig.getWorkerPoolConfig();
		this.size = workerPoolConfig.getPoolSize();
		for (int i = 0; i < workerPoolConfig.getPoolSize(); i++) {
			this.executors.put(i,
					DisruptorAsyncTaskExecutor.createSingleProducerExecutor(workerPoolConfig.getRingBufferSize(), 1,
							String.format(workerPoolConfig.getThreadNamePattern(), i)));
		}
	}

	public void start() {
		for (DisruptorAsyncTaskExecutor executor : this.executors.values()) {
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
		int executorId = roomId % size;
		return this.executors.get(executorId);
	}

	public void shutdown() {
		for (DisruptorAsyncTaskExecutor executor : this.executors.values()) {
			try {
				executor.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
