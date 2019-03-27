package com.mercury.server.processor;

import com.mercury.server.MGSHandler;
import com.nhb.common.BaseLoggable;

public abstract class MGSAbstractProcessor extends BaseLoggable implements MGSProcessor {
	private MGSHandler context;

	public MGSHandler getContext() {
		return context;
	}

	public void setContext(MGSHandler context) {
		this.context = context;
	}

}
