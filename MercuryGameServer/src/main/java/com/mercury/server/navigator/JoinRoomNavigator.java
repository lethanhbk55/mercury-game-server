package com.mercury.server.navigator;

import com.mercury.server.entity.user.User;
import com.mercury.server.plugin.Pluggable;

public interface JoinRoomNavigator extends Pluggable {

	int navigate(User user, int roomId, String password);
}
