package com.mercury.server.extension.config;

import com.mario.config.MarioBaseConfig;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneConfig extends MarioBaseConfig {

	private String handleClass;
	private RoomConfig roomConfig;
	private SchedulerConfig schedulerConfig;
	private PuObject initParams;
	private String joinRoomCallbackClass;
	private String joinRoomNavigatorClass;

	@Override
	protected void _readPuObject(PuObjectRO data) {

	}

}
