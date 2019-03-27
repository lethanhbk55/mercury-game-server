package com.mercury.server.exception;

public class RoomToJoinNotExists extends Throwable {

	private static final long serialVersionUID = 1127242228057397505L;

	public RoomToJoinNotExists(int roomId) {
		super("roomId " + roomId + " doesn't exists");
	}
}
