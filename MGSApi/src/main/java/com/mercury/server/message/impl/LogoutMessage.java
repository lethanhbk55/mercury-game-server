package com.mercury.server.message.impl;

import com.mercury.server.message.MGSAbstractMessage;
import com.mercury.server.message.MGSMessageType;
import com.nhb.common.data.PuArray;

public class LogoutMessage extends MGSAbstractMessage {

	{
		setType(MGSMessageType.LOGOUT);
	}

	@Override
	protected void writePuArray(PuArray array) {

	}

	@Override
	public void readPuArray(PuArray array) {

	}

}
