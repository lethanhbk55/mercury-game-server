package com.mercury.extension.room;

import com.mercury.server.entity.user.User;
import com.mercury.server.exception.MGSException;
import com.mercury.server.plugin.impl.MGSRoomPlugin;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;

public class TestRoomPlugin extends MGSRoomPlugin {

	@Override
	public void userEnter(User user) throws MGSException {
		getLogger().debug("user {} enter room", user.getUsername());
	}

	@Override
	public void userDidEnter(User user) {
		getLogger().debug("user {} did enter room {}", user.getUsername(), getPluginApi().getRoomId());
	}

	@Override
	public void userExit(User user) {
		getLogger().debug("user {} exit room", user.getUsername());
	}

	@Override
	public void request(User user, PuObject params) {
		
	}

	@Override
	public void init(PuObjectRO initParams) {
		
	}

	@Override
	public Object aquireObject(PuObject params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseObject(PuObject params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void userDidExit(User user) {
		// TODO Auto-generated method stub
		
	}

}
