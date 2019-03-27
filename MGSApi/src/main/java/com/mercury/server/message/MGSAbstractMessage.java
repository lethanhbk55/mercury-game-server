package com.mercury.server.message;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;

public abstract class MGSAbstractMessage implements MGSMessage {

	private String zoneName;
	private MGSMessageType type;

	public MGSAbstractMessage() {
		
	}

	protected abstract void writePuArray(PuArray array);

	public abstract void readPuArray(PuArray array);

	@Override
	public PuArray serialize() {
		PuArray array = new PuArrayList();
		array.addFrom(getType().getId());
		array.addFrom(getZoneName());
		writePuArray(array);
		return array;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public MGSMessageType getType() {
		return type;
	}

	public void setType(MGSMessageType type) {
		this.type = type;
	}

}
