package com.mercury.server.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mario.config.LifeCycleConfig;
import com.mario.entity.LifeCycle;
import com.mercury.server.api.PluginApiFactory;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.extension.loader.ExtensionManager;
import com.nhb.common.BaseLoggable;
import com.nhb.common.data.PuObject;

public class PluginManager extends BaseLoggable {

	private Map<String, LifeCycleConfig> lifeCycleConfigByNameMapping;
	private Map<String, LifeCycle> lifeCycles;
	private List<MGSManagedObject> mangedObjects;
	private PluginApiFactory factory;
	private ExtensionManager extensionManager;
	private Zone zone;

	public PluginManager(ExtensionManager extensionManager, PluginApiFactory factory, Zone zone) {
		this.lifeCycles = new HashMap<>();
		this.lifeCycleConfigByNameMapping = new HashMap<>();
		this.mangedObjects = new ArrayList<>();

		this.extensionManager = extensionManager;
		this.factory = factory;
		this.zone = zone;
	}

	public final void init() throws Exception {
		if (extensionManager.isLoaded()) {
			List<LifeCycleConfig> lifeCycleConfigs = extensionManager
					.getLifeCycleConfigByExtension(zone.getExtensionName());

			for (LifeCycleConfig lifeCycleConfig : lifeCycleConfigs) {
				LifeCycle lifeCycle = extensionManager.newInstance(lifeCycleConfig.getExtensionName(),
						lifeCycleConfig.getHandleClass());
				lifeCycle.setName(lifeCycleConfig.getName());

				if (lifeCycle instanceof Pluggable) {
					Pluggable pluggable = (Pluggable) lifeCycle;
					pluggable.setPluginApi(this.factory.newPluginApi(zone.getZoneName()));
					pluggable.setMarioApi(this.factory.newMarioApi());
				}

				if (lifeCycle instanceof MGSManagedObject) {
					mangedObjects.add((MGSManagedObject) lifeCycle);
				}

				lifeCycleConfigByNameMapping.put(lifeCycle.getName(), lifeCycleConfig);
				lifeCycles.put(lifeCycle.getName(), lifeCycle);
			}

			for (MGSManagedObject managedObject : mangedObjects) {
				getLogger().info("[INIT-MANAGED-OBJECT] init {}", managedObject.getName());
				managedObject.init(lifeCycleConfigByNameMapping.get(managedObject.getName()).getInitParams());
				getLogger().info("[INIT-MANAGED-OBJECT] create {} success", managedObject.getName());
			}

			lifeCycles.values().forEach(x -> {
				if (!mangedObjects.contains(x)) {
					getLogger().info("[INIT-LIFE-CYCLE] init {}", x.getName());
					x.init(lifeCycleConfigByNameMapping.get(x.getName()).getInitParams());
					getLogger().info("[INIT-LIFE-CYCLE] create {} success", x.getName());
				}
			});
		}
	}

	public void interop(String name, PuObject params) {
		LifeCycle lifeCycle = this.lifeCycles.get(name);
		if (lifeCycle instanceof MGSInteropable) {
			((MGSInteropable) lifeCycle).interop(params);
		}
	}

	public void request(String name, User user, PuObject params) {
		LifeCycle lifeCycle = this.lifeCycles.get(name);
		if (lifeCycle instanceof Plugin) {
			((Plugin) lifeCycle).request(user, params);
		}
	}

	public Object aquire(String pluginName, PuObject params) {
		LifeCycle lifeCycle = this.lifeCycles.get(pluginName);
		if (lifeCycle instanceof MGSManagedObject) {
			return ((MGSManagedObject) lifeCycle).aquireObject(params);
		}
		return null;
	}

	public void release(String pluginName, PuObject params) {
		LifeCycle lifeCycle = this.lifeCycles.get(pluginName);
		if (lifeCycle instanceof MGSManagedObject) {
			((MGSManagedObject) lifeCycle).releaseObject(params);
		}
	}

	public void onServerReady() {
		for (LifeCycle lifeCycle : this.lifeCycles.values()) {
			lifeCycle.onServerReady();
		}
	}
}
