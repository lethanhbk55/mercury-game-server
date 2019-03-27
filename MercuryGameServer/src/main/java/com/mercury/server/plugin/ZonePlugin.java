package com.mercury.server.plugin;

import com.mercury.server.entity.user.LoginContext;
import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.MGSException;

public interface ZonePlugin extends Plugin, MGSManagedObject {

	void onUserLogin(LoginContext context) throws MGSException;

	void userLoggedIn(User user);

	void userDisconnect(User user, UserDisconnectReason reason);
}
