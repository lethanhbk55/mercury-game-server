package com.mercury.server.processor;

import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.response.MGSResponse;

public interface MGSProcessor {

	MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException;
}
