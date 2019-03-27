package com.mercury.server.test;

import com.mercury.server.api.PluginApiFactory;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.extension.loader.ExtensionInitializer;
import com.mercury.server.extension.loader.ExtensionManager;
import com.nhb.common.utils.Initializer;

public class TestExtensionLoader {
	static {
		Initializer.bootstrap(TestExtensionLoader.class);
	}

	public static void main(String[] args) throws Exception {
		ZoneManager zoneManager = new ZoneManager();
		ExtensionManager extensionManager = new ExtensionManager();
		extensionManager.load();
		PluginApiFactory pluginApiFactory = new PluginApiFactory(extensionManager, null, zoneManager);
		ExtensionInitializer initializer = new ExtensionInitializer(extensionManager, pluginApiFactory, zoneManager,
				null, null);
		initializer.init();
	}
}
