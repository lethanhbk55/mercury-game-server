package com.mercury.server.api;

import com.mario.api.MarioApi;
import com.mario.gateway.socket.SocketSession;
import com.mercury.server.callback.JoinRoomCallback;
import com.mercury.server.entity.room.RoomMessenger;
import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.data.ErrorCode;
import com.mercury.server.response.impl.JoinRoomResponse;
import com.mercury.server.response.impl.LeaveRoomResponse;
import com.nhb.common.BaseLoggable;

class RoomMessengerImpl extends BaseLoggable implements RoomMessenger {
	private MarioApi marioApi;

	public RoomMessengerImpl(MarioApi api) {
		this.marioApi = api;
	}

	@Override
	public void sendJoinRoomSuccess(User user, int roomId) {
		JoinRoomResponse response = new JoinRoomResponse();
		response.setSuccess(true);
		response.setRoomId(roomId);
		SocketSession socketSession = this.marioApi.getSocketSession(user.getSessionId());
		if (socketSession != null) {
			try {
				socketSession.send(response.serialize());
			} catch (Exception e) {
				getLogger().warn("send join room was not success for user {}", user.getUsername(), e);
			}
		} else {
			getLogger().warn("send join room was not success because socket session for user {} not found",
					user.getUsername());
		}
	}

	@Override
	public void sendLeaveRoomSuccess(User user, int roomId, UserLeaveRoomReason reason, ErrorCode errorCode) {
		LeaveRoomResponse response = new LeaveRoomResponse();
		response.setSuccess(true);
		response.setRoomId(roomId);
		response.setReason(reason);
		if (errorCode != null) {
			response.setErrorCode(errorCode.getCode());
			response.setMessage(errorCode.getMessage());
		}
		SocketSession socketSession = this.marioApi.getSocketSession(user.getSessionId());
		if (socketSession != null) {
			try {
				socketSession.send(response.serialize());
			} catch (Exception e) {
				getLogger().error("error while send leave room response for user: {}", user.getUsername(), e);
			}
		} else {
			getLogger().warn("send leave room was not success because socket session for user {} not found",
					user.getUsername());
		}
	}

	@Override
	public void sendJoinRoomFail(User user, int roomId, ErrorCode errorCode, JoinRoomCallback callback) {
		JoinRoomResponse response = new JoinRoomResponse();
		response.setRoomId(roomId);
		response.setSuccess(false);
		response.setErrorCode(errorCode.getCode());
		response.setMessage(errorCode.getMessage());
		if (callback != null) {
			try {
				callback.call(user, roomId, errorCode);
			} catch (Exception e1) {
				getLogger().error("join room callback has exception", e1);
			}
		}
		SocketSession socketSession = this.marioApi.getSocketSession(user.getSessionId());
		if (socketSession != null) {
			socketSession.send(response.serialize());
		}
	}

	@Override
	public void sendLeaveRoomFail(User user, int roomId, ErrorCode errorCode) {
		LeaveRoomResponse response = new LeaveRoomResponse();
		response.setReason(UserLeaveRoomReason.LEAVE_ROOM);
		response.setSuccess(false);
		if (errorCode != null) {
			response.setErrorCode(errorCode.getCode());
			response.setMessage(errorCode.getMessage());
		}
		response.setRoomId(roomId);
		SocketSession socketSession = this.marioApi.getSocketSession(user.getSessionId());
		if (socketSession != null) {
			socketSession.send(response.serialize());
		}
	}

}
