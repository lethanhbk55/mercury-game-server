package com.mercury.server.message;

import java.util.concurrent.atomic.AtomicInteger;

import com.mercury.server.message.impl.JoinRoomMessage;
import com.mercury.server.message.impl.LeaveRoomMessage;
import com.mercury.server.message.impl.LoginMessage;
import com.mercury.server.message.impl.LogoutMessage;
import com.mercury.server.message.impl.RoomPluginMessage;
import com.mercury.server.message.impl.ZonePluginMessage;

public enum MGSMessageType {
	LOGIN(LoginMessage.class),
	LOGOUT(LogoutMessage.class),
	JOIN_ROOM(JoinRoomMessage.class),
	LEAVE_ROOM(LeaveRoomMessage.class),
	ROOM_PLUGIN(RoomPluginMessage.class),
	ZONE_PLUGIN(ZonePluginMessage.class),
	PING(PingMessage.class);

	private int id;
	private Class<? extends MGSMessage> messageClass;

	private MGSMessageType(Class<? extends MGSMessage> messageClass) {
		this.id = genId();
		this.messageClass = messageClass;
	}

	private static AtomicInteger idSeed;

	public static int genId() {
		if (idSeed == null) {
			idSeed = new AtomicInteger();
		}
		return idSeed.incrementAndGet();
	}

	public int getId() {
		return this.id;
	}

	public Class<? extends MGSMessage> getMessageClass() {
		return this.messageClass;
	}

	public static MGSMessageType fromId(int messageId) {
		for (MGSMessageType type : values()) {
			if (type.getId() == messageId) {
				return type;
			}
		}
		return null;
	}
}
