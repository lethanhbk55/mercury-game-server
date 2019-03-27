package com.mercury.server.message.impl;

import com.mercury.server.message.MGSAbstractMessage;
import com.mercury.server.message.MGSMessageType;
import com.nhb.common.data.PuArray;

public class LeaveRoomMessage extends MGSAbstractMessage {

	{
		setType(MGSMessageType.LEAVE_ROOM);
	}

	private int roomId;

	public LeaveRoomMessage() {

	}

	public LeaveRoomMessage(int roomId) {
		this();
		this.roomId = roomId;
	}

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(roomId);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.roomId = array.remove(0).getInteger();
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

}
