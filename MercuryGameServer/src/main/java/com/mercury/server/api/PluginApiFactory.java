package com.mercury.server.api;

import com.mario.api.MarioApi;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.extension.loader.ExtensionManager;

public final class PluginApiFactory {

	private ExtensionManager extensionManager;
	private ZoneManager zoneManager;
	private MarioApi marioApi;

	public PluginApiFactory(ExtensionManager extensionManager, MarioApi marioApi, ZoneManager zoneManager) {
		this.extensionManager = extensionManager;
		this.marioApi = marioApi;
		this.zoneManager = zoneManager;
	}

	public PluginApi newPluginApi(String zoneName) {
		return new PluginApiImpl(extensionManager, zoneManager.getZone(zoneName), this, zoneManager);
	}

	public MarioApi newMarioApi() {
		return this.marioApi;
	}

	PluginApi newRoomPluginApi(String zoneName, Room room) {
		PluginApiImpl impl = new PluginApiImpl(extensionManager, zoneManager.getZone(zoneName), this, zoneManager);
		impl.setRoom(room);
		return impl;
	}
}
