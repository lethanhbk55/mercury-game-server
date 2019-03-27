package com.mercury.server.response.impl;

import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.response.MGSAbstractResponse;
import com.mercury.server.response.MGSResponseType;
import com.nhb.common.data.PuArray;

public class LeaveRoomResponse extends MGSAbstractResponse {

	{
		setType(MGSResponseType.LEAVE_ROOM);
	}

	private boolean success;
	private int roomId;
	private UserLeaveRoomReason reason;
	private int errorCode;
	private String message = "";

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(success);
		array.addFrom(reason.getId());
		array.addFrom(roomId);
		array.addFrom(errorCode);
		array.addFrom(message);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.success = array.remove(0).getBoolean();
		this.setReason(UserLeaveRoomReason.fromId(array.remove(0).getInteger()));
		this.roomId = array.remove(0).getInteger();
		this.errorCode = array.remove(0).getInteger();
		this.message = array.remove(0).getString();
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public UserLeaveRoomReason getReason() {
		return reason;
	}

	public void setReason(UserLeaveRoomReason reason) {
		this.reason = reason;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
