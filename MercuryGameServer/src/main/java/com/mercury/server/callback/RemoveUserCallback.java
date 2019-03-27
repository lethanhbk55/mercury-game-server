package com.mercury.server.callback;

import com.mercury.server.entity.user.User;

public interface RemoveUserCallback {

	void apply(User user);
}
