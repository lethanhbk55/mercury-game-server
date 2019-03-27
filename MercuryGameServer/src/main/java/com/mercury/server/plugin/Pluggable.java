package com.mercury.server.plugin;

import com.mario.api.MarioApi;
import com.mercury.server.api.PluginApi;

public interface Pluggable {
	
	PluginApi getPluginApi();
	
	void setPluginApi(PluginApi pluginApi);
	
	MarioApi getMarioApi();
	
	void setMarioApi(MarioApi marioApi);
}
