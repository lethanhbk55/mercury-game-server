package com.mercury.server.interop;

import com.mercury.server.interop.message.MGSInteropMessage;

public interface MGSServerApi {

	void call(MGSInteropMessage message);
}
