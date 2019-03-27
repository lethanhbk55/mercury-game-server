	package com.mercury.server.message.impl;

import com.mercury.server.message.MGSAbstractMessage;
import com.mercury.server.message.MGSMessageType;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;

public class ZonePluginMessage extends MGSAbstractMessage {
	{
		setType(MGSMessageType.ZONE_PLUGIN);
	}

	private String pluginName;
	private PuObject params;

	public ZonePluginMessage() {

	}

	public ZonePluginMessage(String pluginName, PuObject params) {
		this();
		this.pluginName = pluginName;
		this.params = params;
	}

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
