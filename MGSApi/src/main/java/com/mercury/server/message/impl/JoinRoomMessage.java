package com.mercury.server.message.impl;

import com.mercury.server.message.MGSAbstractMessage;
import com.mercury.server.message.MGSMessageType;
import com.nhb.common.data.PuArray;

public class JoinRoomMessage extends MGSAbstractMessage {

	{
		setType(MGSMessageType.JOIN_ROOM);
	}

	private int roomId;
	private String password;

	public JoinRoomMessage() {

	}

	public JoinRoomMessage(int roomId) {
		this();
		this.roomId = roomId;
	}

	public JoinRoomMessage(int roomId, String password) {
		this(roomId);
		this.password = password;
	}

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(roomId);
		array.addFrom(password);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.roomId = array.remove(0).getInteger();
		this.password = array.remove(0).getString();
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
