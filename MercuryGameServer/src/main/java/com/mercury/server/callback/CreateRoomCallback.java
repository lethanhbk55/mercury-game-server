package com.mercury.server.callback;

import com.mercury.server.entity.room.Room;

public interface CreateRoomCallback {

	void call(Room room);
}
