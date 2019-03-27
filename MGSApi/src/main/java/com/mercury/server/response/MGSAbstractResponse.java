package com.mercury.server.response;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;

public abstract class MGSAbstractResponse implements MGSResponse {
	private MGSResponseType type;
	private int messageId;

	@Override
	public PuArray serialize() {
		PuArray array = new PuArrayList();
		array.addFrom(getType().getId());
		writePuArray(array);
		return array;
	}

	protected abstract void writePuArray(PuArray array);
	
	public abstract void readPuArray(PuArray array);

	public MGSResponseType getType() {
		return type;
	}

	public void setType(MGSResponseType type) {
		this.type = type;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
}
