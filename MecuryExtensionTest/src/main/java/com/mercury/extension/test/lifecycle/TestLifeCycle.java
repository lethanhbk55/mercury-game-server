package com.mercury.extension.test.lifecycle;

import com.mercury.server.plugin.impl.MGSLifeCycle;
import com.nhb.common.data.PuObjectRO;

public class TestLifeCycle extends MGSLifeCycle {

	@Override
	public void init(PuObjectRO initParams) {
		getLogger().debug("init life cycle {} with params: {}", getName(), initParams);
	}

}
