package com.mercury.server.plugin;

import com.mercury.server.entity.user.User;
import com.mercury.server.exception.MGSException;

public interface RoomPlugin extends Plugin, MGSManagedObject {

	void userEnter(User user) throws MGSException;

	void userDidEnter(User user);

	void userExit(User user) throws MGSException;
	
	void userDidExit(User user);
}
