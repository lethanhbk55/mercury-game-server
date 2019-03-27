package com.mercury.server.processor.impl;

import com.mercury.server.annotation.CommandProcessor;
import com.mercury.server.entity.room.AbstractRoom;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.MGSException;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.exception.data.RoomError;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.message.impl.LeaveRoomMessage;
import com.mercury.server.processor.MGSAbstractProcessor;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.impl.LeaveRoomResponse;

@CommandProcessor(type = MGSMessageType.LEAVE_ROOM)
public class LeaveRoom extends MGSAbstractProcessor {

	@Override
	public MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException {
		LeaveRoomMessage request = (LeaveRoomMessage) message;
		Zone zone = getContext().getZoneManager().getZone(request.getZoneName());
		if (zone == null) {
			throw new ProcessMessageException("zone to leave was not found");
		}
		Room room = zone.findRoomById(request.getRoomId());
		if (room == null) {
			LeaveRoomResponse response = new LeaveRoomResponse();
			response.setReason(UserLeaveRoomReason.LEAVE_ROOM);
			response.setSuccess(false);
			response.setErrorCode(RoomError.ROOM_DOES_NOT_EXIT.getCode());
			response.setRoomId(request.getRoomId());
			return response;
		}

		User user = zone.getUserManager().getBySessionId(sessionId);
		if (user == null) {
			throw new ProcessMessageException("user was not join to zone but want to leave room");
		}

		if (room instanceof AbstractRoom) {
			try {
				((AbstractRoom) room).leaveRoom(user, UserLeaveRoomReason.LEAVE_ROOM, null);
			} catch (MGSException e) {
				getLogger().error("user leave room get error", e);
				LeaveRoomResponse response = new LeaveRoomResponse();
				response.setReason(UserLeaveRoomReason.LEAVE_ROOM);
				response.setSuccess(false);
				if (e.getErrorCode() != null) {
					response.setErrorCode(e.getErrorCode().getCode());
					response.setMessage(e.getErrorCode().getMessage());
				}
				response.setRoomId(request.getRoomId());
				return response;
			} catch (Exception e) {
				throw new RuntimeException("user leave room error", e);
			}
		}

		return null;
	}

}
