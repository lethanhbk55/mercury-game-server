package com.mercury.server.interop.message.impl;

import com.mercury.server.interop.message.MGSAbstractInteropMessage;
import com.mercury.server.interop.message.MGSInteropMessageType;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;

public class InteropPluginMessage extends MGSAbstractInteropMessage {
	{
		setType(MGSInteropMessageType.PLUGIN_MESSAGE);
	}

	private String pluginName;
	private PuObject params;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(pluginName);
		array.addFrom(params);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.pluginName = array.remove(0).getString();
		this.params = array.remove(0).getPuObject();
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public PuObject getParams() {
		return params;
	}

	public void setParams(PuObject params) {
		this.params = params;
	}

}
