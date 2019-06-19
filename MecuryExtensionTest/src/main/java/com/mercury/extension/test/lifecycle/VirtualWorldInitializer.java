package com.mercury.extension.test.lifecycle;

import com.mercury.extension.room.TestRoomPlugin;
import com.mercury.server.api.setting.CreateRoomSetting;
import com.mercury.server.callback.CreateRoomCallback;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.room.RoomRemoveMode;
import com.mercury.server.plugin.impl.MGSLifeCycle;
import com.nhb.common.data.PuObjectRO;

public class VirtualWorldInitializer extends MGSLifeCycle {

	@Override
	public void init(PuObjectRO initParams) {
		CreateRoomSetting setting = CreateRoomSetting.newBuilder().maxUser(5).removeMode(RoomRemoveMode.NERVER_REMOVE)
				.pluginClass(TestRoomPlugin.class.getName()).roomName("TestRoom")
				.createRoomCallback(new CreateRoomCallback() {

					@Override
					public void call(Room room) {
						System.out.println("room callback " + room.getRoomId());
					}
				}).build();
		getPluginApi().createRoom(setting);
	}

}
