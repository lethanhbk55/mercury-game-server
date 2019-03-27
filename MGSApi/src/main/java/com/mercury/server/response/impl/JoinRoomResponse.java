package com.mercury.server.response.impl;

import com.mercury.server.response.MGSAbstractResponse;
import com.mercury.server.response.MGSResponseType;
import com.nhb.common.data.PuArray;

public class JoinRoomResponse extends MGSAbstractResponse {

	{
		setType(MGSResponseType.JOIN_ROOM);
	}

	private boolean success;
	private int errorCode;
	private int roomId;
	private String message;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(success);
		array.addFrom(errorCode);
		array.addFrom(roomId);
		array.addFrom(message);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.success = array.remove(0).getBoolean();
		this.errorCode = array.remove(0).getInteger();
		this.roomId = array.remove(0).getInteger();
		this.message = array.remove(0).getString();
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
