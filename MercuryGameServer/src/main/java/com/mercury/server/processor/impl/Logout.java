package com.mercury.server.processor.impl;

import com.mercury.server.annotation.CommandProcessor;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.message.impl.LogoutMessage;
import com.mercury.server.processor.MGSAbstractProcessor;
import com.mercury.server.response.MGSResponse;

@CommandProcessor(type = MGSMessageType.LOGOUT)
public class Logout extends MGSAbstractProcessor {

	@Override
	public MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException {
		LogoutMessage logoutMessage = (LogoutMessage) message;
		String zoneName = logoutMessage.getZoneName();
		Zone zone = getContext().getZoneManager().getZone(zoneName);

		if (zone == null) {
			throw new ProcessMessageException("request zone " + message.getZoneName() + " doesn't exists");
		}

		User user = zone.getUserManager().getBySessionId(sessionId);
		if (user != null) {
			zone.getUserManager().removeUser(user.getUsername(), UserDisconnectReason.LOGOUT);
			getContext().getSessionManager().removeSession(sessionId);
		}

		return null;
	}

}
