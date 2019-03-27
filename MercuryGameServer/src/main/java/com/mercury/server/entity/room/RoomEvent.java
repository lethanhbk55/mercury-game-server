package com.mercury.server.entity.room;

import com.mercury.server.entity.user.User;
import com.nhb.eventdriven.impl.AbstractEvent;

class RoomEvent extends AbstractEvent {

	static final String JOIN = "join";
	static final String LEAVE = "leave";

	private User user;
	private Room room;

	public RoomEvent(String type, User user, Room room) {
		setType(type);
		setUser(user);
		this.setRoom(room);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

}
