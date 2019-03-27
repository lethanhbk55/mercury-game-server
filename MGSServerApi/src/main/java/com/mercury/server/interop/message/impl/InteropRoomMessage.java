package com.mercury.server.interop.message.impl;

import com.mercury.server.interop.message.MGSAbstractInteropMessage;
import com.mercury.server.interop.message.MGSInteropMessageType;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;

public class InteropRoomMessage extends MGSAbstractInteropMessage {

	{
		setType(MGSInteropMessageType.ROOM_MESSAGE);
	}

	private int roomId;
	private PuObject params;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(getRoomId());
		array.addFrom(getParams());
	}

	@Override
	public void readPuArray(PuArray array) {
		this.setRoomId(array.remove(0).getInteger());
		this.setParams(array.remove(0).getPuObject());
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
