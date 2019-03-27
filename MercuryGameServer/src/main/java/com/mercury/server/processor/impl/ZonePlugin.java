package com.mercury.server.processor.impl;

import com.mercury.server.annotation.CommandProcessor;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.message.impl.ZonePluginMessage;
import com.mercury.server.processor.MGSAbstractProcessor;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.impl.LogoutResponse;

@CommandProcessor(type = MGSMessageType.ZONE_PLUGIN)
public class ZonePlugin extends MGSAbstractProcessor {

	@Override
	public MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException {
		ZonePluginMessage request = (ZonePluginMessage) message;
		Zone zone = getContext().getZoneManager().getZone(message.getZoneName());
		if (zone == null) {
			throw new ProcessMessageException("request zone " + message.getZoneName() + " doesn't exists");
		}

		User user = zone.getUserManager().getBySessionId(sessionId);

		if (user == null) {
			LogoutResponse response = new LogoutResponse();
			response.setReason(UserDisconnectReason.UNKNOWN);
			response.setSuccess(true);
			getLogger().warn("user was not join to zone but want to send zone request");
			return response;
		}

		try {
			zone.getPluginManager().request(request.getPluginName(), user, request.getParams());
		} catch (Exception e) {
			getLogger().error("zone plugin request has exception", e);
		}

		return null;
	}

}
