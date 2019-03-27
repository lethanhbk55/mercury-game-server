package com.mercury.server.response.impl;

import com.mercury.server.response.MGSAbstractResponse;
import com.mercury.server.response.MGSResponseType;
import com.nhb.common.data.PuArray;

public class PingResponse extends MGSAbstractResponse {
	{
		setType(MGSResponseType.PING);
	}

	private int pingId;
	private long timestamp;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(pingId);
		array.addFrom(timestamp);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.pingId = array.remove(0).getInteger();
		this.timestamp = array.remove(0).getLong();
	}

	public int getPingId() {
		return pingId;
	}

	public void setPingId(int pingId) {
		this.pingId = pingId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
