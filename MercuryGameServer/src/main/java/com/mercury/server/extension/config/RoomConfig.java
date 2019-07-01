package com.mercury.server.extension.config;

import com.mario.config.WorkerPoolConfig;

import lombok.Setter;
import lombok.ToString;
import lombok.Getter;

@Getter
@Setter
@ToString
public class RoomConfig {

	private WorkerPoolConfig workerPoolConfig;
}
