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
import com.mercury.server.message.impl.JoinRoomMessage;
import com.mercury.server.processor.MGSAbstractProcessor;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.impl.JoinRoomResponse;

@CommandProcessor(type = MGSMessageType.JOIN_ROOM)
public class JoinRoom extends MGSAbstractProcessor {

	@Override
	public MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException {
		JoinRoomMessage request = (JoinRoomMessage) message;
		Zone zone = getContext().getZoneManager().getZone(request.getZoneName());

		if (zone == null) {
			throw new ProcessMessageException("request zone " + message.getZoneName() + " doesn't exists");
		}

		int roomId = request.getRoomId();

		User user = zone.getUserManager().getBySessionId(sessionId);
		if (user == null) {
			throw new ProcessMessageException("User not join to zone but want to join room, sessionId: " + sessionId);
		}
		
		if (zone.getJoinRoomNavigator() != null) {
			roomId = zone.getJoinRoomNavigator().navigate(user, roomId);
		}

		Room room = zone.getRoomManager().findRoomById(roomId);
		if (room == null) {
			JoinRoomResponse response = new JoinRoomResponse();
			response.setRoomId(request.getRoomId());
			response.setSuccess(false);
			response.setErrorCode(RoomError.ROOM_DOES_NOT_EXIT.getCode());
			response.setMessage(RoomError.ROOM_DOES_NOT_EXIT.getMessage());
			return response;
		}


		if (room.hasPassword()) {
			String password = request.getPassword();
			if (!password.equals(room.getPassword())) {
				JoinRoomResponse response = new JoinRoomResponse();
				response.setRoomId(room.getRoomId());
				response.setSuccess(false);
				response.setErrorCode(RoomError.WRONG_PASSWORD.getCode());
				response.setMessage(RoomError.WRONG_PASSWORD.getMessage());
				if (zone.getJoinRoomCallback() != null) {
					try {
						zone.getJoinRoomCallback().call(user, request.getRoomId(), RoomError.WRONG_PASSWORD);
					} catch (Exception e) {
						getLogger().error("join room callback has exception", e);
					}
				}
				return response;
			}
		}

		try {
			if (user.getLastJoinedRoom() != null && user.getLastJoinedRoom() != room) {
				AbstractRoom lastJoinedRoom = (AbstractRoom) user.getLastJoinedRoom();
				lastJoinedRoom.leaveRoom(user, UserLeaveRoomReason.KICKED, null);
			}

			if (room instanceof AbstractRoom) {
				((AbstractRoom) room).joinRoom(user);
			}
		} catch (MGSException e) {
			getLogger().warn("user join room get error {}", e.getErrorCode());
			JoinRoomResponse response = new JoinRoomResponse();
			response.setRoomId(request.getRoomId());
			response.setSuccess(false);
			response.setErrorCode(e.getErrorCode().getCode());
			response.setMessage(e.getErrorCode().getMessage());
			if (zone.getJoinRoomCallback() != null) {
				try {
					zone.getJoinRoomCallback().call(user, request.getRoomId(), e.getErrorCode());
				} catch (Exception e1) {
					getLogger().error("join room callback has exception", e1);
				}
			}
			return response;
		} catch (Exception e) {
			throw new RuntimeException("user join room error", e);
		}

		return null;
	}

}
