package com.mercury.server.utils;

import java.util.Set;

import com.mario.api.MarioApi;
import com.mario.gateway.socket.SocketSession;
import com.mario.schedule.ScheduledCallback;
import com.mercury.server.entity.room.AbstractRoom;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.room.RoomManager;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.data.RoomError;
import com.nhb.common.BaseLoggable;

public class RoomHunter extends BaseLoggable {
	private ZoneManager zoneManager;
	private MarioApi marioApi;

	public RoomHunter(ZoneManager zoneManager, MarioApi marioApi) {
		this.zoneManager = zoneManager;
		this.marioApi = marioApi;
	}

	public void start() {
		this.marioApi.getScheduler().scheduleAtFixedRate(60000, 60000, new ScheduledCallback() {

			@Override
			public void call() {
				hunt();
			}
		});
	}

	private void hunt() {
		for (Zone zone : zoneManager.getZones()) {
			RoomManager roomManager = zone.getRoomManager();
			for (Room room : roomManager.getRooms()) {
				Set<User> users = room.getUsers();
				if (room instanceof AbstractRoom) {
					for (User user : users) {
						SocketSession socketSession = marioApi.getSocketSession(user.getSessionId());
						if (socketSession == null) {
							try {
								((AbstractRoom) room).leaveRoom(user, UserLeaveRoomReason.KICKED,
										RoomError.USER_DISCONNECT);
							} catch (Exception e) {
								getLogger().warn("user {} disconnect to leave room", user.getUsername());
							}
						}
					}
				}
			}
		}
	}

	public void stop() {

	}
}
