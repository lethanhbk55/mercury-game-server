package com.mercury.server.extension.loader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mario.config.LifeCycleConfig;
import com.mercury.server.extension.config.ZoneConfig;
import com.nhb.common.BaseLoggable;
import com.nhb.common.utils.FileSystemUtils;

public final class ExtensionManager extends BaseLoggable {

	private static final String extensionsFolder = "plugins";
	private Map<String, ExtensionLoader> extensionLoaderByName;
	private boolean loaded = false;

	public boolean isLoaded() {
		return this.loaded;
	}

	public void load() throws Exception {
		File file = new File(
				FileSystemUtils.createPathFrom(FileSystemUtils.getBasePathForClass(getClass()), extensionsFolder));
		if (file.exists() && file.isDirectory()) {
			this.extensionLoaderByName = new HashMap<String, ExtensionLoader>();
			File[] children = file.listFiles();
			for (File ext : children) {
				if (ext.isDirectory() && !ext.getName().equalsIgnoreCase("__lib__")) {
					ExtensionLoader loader = new ExtensionLoader(ext);
					loader.load();
					this.extensionLoaderByName.put(loader.getName(), loader);
				}
			}
			this.loaded = true;
		}
	}

	public <T> T newInstance(String extensionName, String className) {
		if (extensionName != null && extensionName.trim().length() > 0) {
			ExtensionLoader loader = this.extensionLoaderByName.get(extensionName);
			if (loader != null) {
				if (className != null && className.trim().length() > 0) {
					try {
						return loader.newInstance(className.trim());
					} catch (Exception e) {
						getLogger().error("cannot create new instance for class name: {}, extension name: {}",
								className, extensionName, e);
					}
				} else {
					getLogger().error("class name cannot be empty");
				}
			} else {
				getLogger().error("Extension loader cannot be found");
			}
		} else {
			getLogger().error("no extension is loaded");
		}
		return null;
	}

	public List<ZoneConfig> getZoneConfigs() {
		if (this.isLoaded()) {
			List<ZoneConfig> zoneConfigs = new ArrayList<>();
			for (ExtensionLoader loader : this.extensionLoaderByName.values()) {
				zoneConfigs.add(loader.getConfigReader().getZoneConfig());
			}
			return zoneConfigs;
		}
		return null;
	}

	public List<LifeCycleConfig> getLifeCycleConfigByExtension(String extensionName) {
		if (this.isLoaded()) {
			ExtensionLoader loader = this.extensionLoaderByName.get(extensionName);
			if (loader != null) {
				return loader.getConfigReader().getLifeCycleConfigs();
			}
		}
		return null;
	}
}
