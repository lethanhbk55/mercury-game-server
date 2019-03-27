package com.mercury.server.processor.impl;

import com.mercury.server.annotation.CommandProcessor;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.message.PingMessage;
import com.mercury.server.processor.MGSAbstractProcessor;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.impl.PingResponse;

@CommandProcessor(type = MGSMessageType.PING)
public class Ping extends MGSAbstractProcessor {

	@Override
	public MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException {
		getContext().getPingManager().updatePing(sessionId);
		PingMessage pingMessage = (PingMessage) message;
		PingResponse response = new PingResponse();
		response.setPingId(pingMessage.getId());
		response.setTimestamp(pingMessage.getTimestamp());
		return response;
	}

}
