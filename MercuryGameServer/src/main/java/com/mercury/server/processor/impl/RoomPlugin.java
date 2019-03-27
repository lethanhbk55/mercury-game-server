package com.mercury.server.processor.impl;

import com.mercury.server.annotation.CommandProcessor;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.message.impl.RoomPluginMessage;
import com.mercury.server.processor.MGSAbstractProcessor;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.impl.LogoutResponse;

@CommandProcessor(type = MGSMessageType.ROOM_PLUGIN)
public class RoomPlugin extends MGSAbstractProcessor {

	@Override
	public MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException {
		RoomPluginMessage request = (RoomPluginMessage) message;
		Zone zone = getContext().getZoneManager().getZone(request.getZoneName());

		if (zone == null) {
			throw new ProcessMessageException("request zone " + message.getZoneName() + " doesn't exists");
		}

		Room room = zone.getRoomManager().findRoomById(request.getRoomId());
		User user = zone.getUserManager().getBySessionId(sessionId);

		if (user == null) {
			LogoutResponse response = new LogoutResponse();
			response.setReason(UserDisconnectReason.UNKNOWN);
			response.setSuccess(true);
			getLogger().warn("user was not join to zone but want to send room request");
			return response;
		}

		if (room == null) {
			throw new ProcessMessageException("request room doesn't exit");
		}

		try {
			room.getRoomPlugin().request(user, request.getParams());
		} catch (Exception e) {
			getLogger().error("room plugin request has exception", e);
		}
		return null;
	}

}
