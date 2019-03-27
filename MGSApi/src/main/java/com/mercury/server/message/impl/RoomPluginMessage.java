package com.mercury.server.message.impl;

import com.mercury.server.message.MGSAbstractMessage;
import com.mercury.server.message.MGSMessageType;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;

public class RoomPluginMessage extends MGSAbstractMessage {

	{
		setType(MGSMessageType.ROOM_PLUGIN);
	}

	private int roomId;
	private PuObject params;

	public RoomPluginMessage() {

	}

	public RoomPluginMessage(int roomId, PuObject params) {
		this();
		this.roomId = roomId;
		this.params = params;
	}

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(getRoomId());
		array.addFrom(params);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.setRoomId(array.remove(0).getInteger());
		this.params = array.remove(0).getPuObject();
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public PuObject getParams() {
		return params;
	}

	public void setParams(PuObject params) {
		this.params = params;
	}

}
