package com.mercury.server.message;

import com.nhb.common.data.PuArray;

public class PingMessage extends MGSAbstractMessage {
	private static int idSeed = 0;

	{
		setType(MGSMessageType.PING);
	}

	private int id;
	private long timestamp;

	public PingMessage() {
		autoId();
		autoTimestamp();
	}

	public PingMessage(int id, long timestamp) {
		setId(id);
		setTimestamp(timestamp);
	}

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(id);
		array.addFrom(timestamp);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.id = array.remove(0).getInteger();
		this.timestamp = array.remove(0).getLong();
	}

	public void autoTimestamp() {
		this.timestamp = System.currentTimeMillis();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void autoId() {
		this.id = idSeed++;
	}
}
