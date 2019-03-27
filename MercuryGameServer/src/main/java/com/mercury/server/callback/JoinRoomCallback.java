package com.mercury.server.callback;

import com.mercury.server.entity.user.User;
import com.mercury.server.exception.data.ErrorCode;
import com.mercury.server.plugin.Pluggable;

public interface JoinRoomCallback extends Pluggable {

	void call(User user, int roomId, ErrorCode error) throws Exception;
}
