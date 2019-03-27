package com.mercury.server.plugin;

import com.mario.entity.LifeCycle;
import com.nhb.common.data.PuObject;

public interface MGSManagedObject extends LifeCycle {

	Object aquireObject(PuObject params);

	void releaseObject(PuObject params);
}
