package com.mercury.server.interop.message;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuArrayList;

public abstract class MGSAbstractInteropMessage implements MGSInteropMessage {
	private MGSInteropMessageType type;
	private String zoneName;

	@Override
	public PuArray serialize() {
		PuArray array = new PuArrayList();
		array.addFrom(this.getType().getId());
		array.addFrom(this.getZoneName());
		writePuArray(array);
		return array;
	}

	protected abstract void writePuArray(PuArray array);

	public abstract void readPuArray(PuArray array);

	public MGSInteropMessageType getType() {
		return type;
	}

	public void setType(MGSInteropMessageType type) {
		this.type = type;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
}
