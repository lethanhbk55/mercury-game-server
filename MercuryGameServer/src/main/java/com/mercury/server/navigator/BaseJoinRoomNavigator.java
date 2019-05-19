package com.mercury.server.navigator;

import com.mario.api.MarioApi;
import com.mercury.server.api.PluginApi;
import com.nhb.common.BaseLoggable;

public abstract class BaseJoinRoomNavigator extends BaseLoggable implements JoinRoomNavigator {

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
}
