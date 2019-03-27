package com.mercury.server.plugin.impl;

import com.mario.api.MarioApi;
import com.mario.entity.LifeCycle;
import com.mercury.server.api.PluginApi;
import com.mercury.server.plugin.Pluggable;
import com.nhb.common.BaseLoggable;

public abstract class MGSLifeCycle extends BaseLoggable implements LifeCycle, Pluggable {

	private String extensionName;
	private String name;
	private MarioApi marioApi;
	private PluginApi pluginApi;

	public MarioApi getMarioApi() {
		return marioApi;
	}

	public void setMarioApi(MarioApi marioApi) {
		this.marioApi = marioApi;
	}

	public PluginApi getPluginApi() {
		return pluginApi;
	}

	public void setPluginApi(PluginApi pluginApi) {
		this.pluginApi = pluginApi;
	}

	public String getExtensionName() {
		return extensionName;
	}

	public void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void destroy() throws Exception {

	}

	@Override
	public void onServerReady() {

	}

	public void onExtensionReady() {
		
	};
}
