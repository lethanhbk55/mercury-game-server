package com.mercury.server.plugin.impl;

import com.mercury.server.plugin.ZonePlugin;
import com.nhb.common.data.PuObject;

public abstract class MGSZonePlugin extends MGSPlugin implements ZonePlugin {

	@Override
	public Object aquireObject(PuObject params) {
		return null;
	}
	
	@Override
	public void releaseObject(PuObject params) {
		
	}
}
