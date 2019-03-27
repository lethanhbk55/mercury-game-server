package com.mercury.server.interop;

import com.mario.api.MarioApi;
import com.mercury.server.interop.message.MGSInteropMessage;

public class MGSInteropApi implements MGSServerApi {
	private static final String MERCURY_SERVER_HANDLER = "mercury-server-handler";

	private MarioApi api;

	public MGSInteropApi(MarioApi api) {
		this.api = api;
	}

	public void call(MGSInteropMessage message) {
		this.api.call(MERCURY_SERVER_HANDLER, message.serialize());
	}
}
