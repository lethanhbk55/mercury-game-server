package com.mercury.server.callback;

import com.mercury.server.entity.room.Room;

public interface RemoveRoomCallback {
	
	void call(Room room);
}
