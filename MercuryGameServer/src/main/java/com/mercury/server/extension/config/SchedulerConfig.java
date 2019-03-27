package com.mercury.server.extension.config;

import com.mario.config.WorkerPoolConfig;

public class SchedulerConfig {

	private int counter;
	private WorkerPoolConfig workerPoolConfig;

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public WorkerPoolConfig getWorkerPoolConfig() {
		return workerPoolConfig;
	}

	public void setWorkerPoolConfig(WorkerPoolConfig workerPoolConfig) {
		this.workerPoolConfig = workerPoolConfig;
	}

}
