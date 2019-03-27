package com.mercury.server.response.impl;

import com.mercury.server.response.MGSAbstractResponse;
import com.mercury.server.response.MGSResponseType;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;

public class ExtensionResponse extends MGSAbstractResponse {

	{
		setType(MGSResponseType.EXTENSION);
	}

	private PuObject data;

	public ExtensionResponse() {

	}

	public ExtensionResponse(PuObject data) {
		this();
		this.data = data;
	}

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(data);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.data = array.remove(0).getPuObject();
	}

	public PuObject getData() {
		return data;
	}

	public void setData(PuObject data) {
		this.data = data;
	}

}
