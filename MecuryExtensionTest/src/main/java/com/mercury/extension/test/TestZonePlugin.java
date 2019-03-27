package com.mercury.extension.test;

import com.mercury.server.entity.user.LoginContext;
import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.MGSException;
import com.mercury.server.plugin.impl.MGSZonePlugin;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;

public class TestZonePlugin extends MGSZonePlugin {

	@Override
	public void onUserLogin(LoginContext context) throws MGSException {
		getLogger().debug("on user login {} - {} - {}", context.getUsername(), context.getPassword(),
				context.getParams());
	}

	@Override
	public void userLoggedIn(User user) {
		getLogger().debug("user logged in: {}", user.getUsername());
	}

	@Override
	public void userDisconnect(User user, UserDisconnectReason reason) {
		getLogger().debug("user {} disconnect in zone, reason {}", user.getUsername(), reason);
	}

	@Override
	public void request(User user, PuObject params) {
		
	}

	@Override
	public void init(PuObjectRO initParams) {
		
	}

}
