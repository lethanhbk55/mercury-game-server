package com.mercury.server.entity.room;

import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.data.ErrorCode;

public interface RoomMessenger {

	void sendJoinRoomSuccess(User user, int roomId);

	void sendLeaveRoomSuccess(User user, int roomId, UserLeaveRoomReason reason, ErrorCode code);
}
