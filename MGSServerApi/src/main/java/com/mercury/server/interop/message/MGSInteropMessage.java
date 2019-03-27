package com.mercury.server.interop.message;

import com.nhb.common.data.PuArray;

public interface MGSInteropMessage {

	PuArray serialize();

	MGSInteropMessageType getType();

	String getZoneName();
}
