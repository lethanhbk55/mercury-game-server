package com.mercury.server.entity.room;

import com.mercury.server.api.setting.CreateRoomSetting;

class RoomManagerEvent {

	static enum EventType {
		ADD,
		REMOVE
	}

	private EventType eventType;
	private Room room;
	private int roomId;
	private CreateRoomSetting setting;

	public RoomManagerEvent() {

	}

	public RoomManagerEvent(EventType eventType, int roomId) {
		this();
		setEventType(eventType);
		setRoomId(roomId);
	}

	public RoomManagerEvent(EventType eventType, Room room) {
		setEventType(eventType);
		setRoom(room);
	}

	public RoomManagerEvent(EventType eventType, Room room, CreateRoomSetting setting) {
		this(eventType, room);
		setSetting(setting);
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public CreateRoomSetting getSetting() {
		return setting;
	}

	public void setSetting(CreateRoomSetting setting) {
		this.setting = setting;
	}

}
