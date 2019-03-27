package com.mercury.server;

import com.mario.entity.impl.BaseMessageHandler;
import com.mario.entity.message.Message;
import com.nhb.common.data.PuElement;

public class MGSForwardMessageHandler extends BaseMessageHandler {

	@Override
	public PuElement handle(Message mgs) {
		PuElement requestParams = mgs.getData();
		getLogger().debug("receive data: {}", requestParams);

		getApi().call("mercury-server-handler", requestParams);
		return null;
	}
}
