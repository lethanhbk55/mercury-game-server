package com.mercury.server.entity.room;

import com.mercury.server.callback.JoinRoomCallback;
import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.data.ErrorCode;

public interface RoomMessenger {

	void sendJoinRoomSuccess(User user, int roomId);

	void sendJoinRoomFail(User user, int roomId, ErrorCode errorCode, JoinRoomCallback joinRoomCallback);

	void sendLeaveRoomSuccess(User user, int roomId, UserLeaveRoomReason reason, ErrorCode code);

	void sendLeaveRoomFail(User user, int roomId, ErrorCode errorCode);
}
