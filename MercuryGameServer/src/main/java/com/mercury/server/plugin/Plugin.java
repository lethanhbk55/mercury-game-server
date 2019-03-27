package com.mercury.server.plugin;

import com.mario.entity.LifeCycle;
import com.mercury.server.entity.user.User;
import com.nhb.common.data.PuObject;

public interface Plugin extends LifeCycle, Pluggable, MGSInteropable {

	void request(User user, PuObject params);

}
