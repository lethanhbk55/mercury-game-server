package com.mercury.server.extension.config;

import com.mario.config.MarioBaseConfig;
import com.mario.config.WorkerPoolConfig;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;

public class ZoneConfig extends MarioBaseConfig {

	private String handleClass;
	private WorkerPoolConfig workerPoolConfig;
	private SchedulerConfig schedulerConfig;
	private PuObject initParams;
	private String joinRoomCallbackClass;
	private String joinRoomNavigatorClass;

	public String getHandleClass() {
		return handleClass;
	}

	public void setHandleClass(String handleClass) {
		this.handleClass = handleClass;
	}

	public WorkerPoolConfig getWorkerPoolConfig() {
		return workerPoolConfig;
	}

	public void setWorkerPoolConfig(WorkerPoolConfig workerPoolConfig) {
		this.workerPoolConfig = workerPoolConfig;
	}

	public PuObject getInitParams() {
		return initParams;
	}

	public void setInitParams(PuObject initParams) {
		this.initParams = initParams;
	}

	public SchedulerConfig getSchedulerConfig() {
		return schedulerConfig;
	}

	public void setSchedulerConfig(SchedulerConfig schedulerConfig) {
		this.schedulerConfig = schedulerConfig;
	}

	@Override
	protected void _readPuObject(PuObjectRO data) {

	}

	public String getJoinRoomCallbackClass() {
		return joinRoomCallbackClass;
	}

	public void setJoinRoomCallbackClass(String joinRoomCallbackClass) {
		this.joinRoomCallbackClass = joinRoomCallbackClass;
	}

	public String getJoinRoomNavigatorClass() {
		return joinRoomNavigatorClass;
	}

	public void setJoinRoomNavigatorClass(String joinRoomNavigator) {
		this.joinRoomNavigatorClass = joinRoomNavigator;
	}

}
