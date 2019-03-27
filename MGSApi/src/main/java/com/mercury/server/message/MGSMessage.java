package com.mercury.server.message;

import com.nhb.common.data.PuArray;

public interface MGSMessage {

	PuArray serialize();

	String getZoneName();

	MGSMessageType getType();
}
