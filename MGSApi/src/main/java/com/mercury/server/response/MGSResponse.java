package com.mercury.server.response;

import com.nhb.common.data.PuArray;

public interface MGSResponse {

	PuArray serialize();

	MGSResponseType getType();
	
	int getMessageId();
}
