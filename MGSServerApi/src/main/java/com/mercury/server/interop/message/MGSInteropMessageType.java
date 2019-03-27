package com.mercury.server.interop.message;

import java.util.concurrent.atomic.AtomicInteger;

import com.mercury.server.interop.message.impl.InteropPluginMessage;
import com.mercury.server.interop.message.impl.InteropRoomMessage;

public enum MGSInteropMessageType {
	ROOM_MESSAGE(InteropRoomMessage.class),
	PLUGIN_MESSAGE(InteropPluginMessage.class);

	private static AtomicInteger idSeed;

	private int genId() {
		if (idSeed == null) {
			idSeed = new AtomicInteger();
		}
		return idSeed.incrementAndGet();
	}

	private int id;
	private Class<? extends MGSInteropMessage> messageClass;

	private MGSInteropMessageType(Class<? extends MGSInteropMessage> messageClass) {
		this.id = genId();
		this.messageClass = messageClass;
	}

	public static final MGSInteropMessageType fromId(int typeId) {
		for (MGSInteropMessageType type : values()) {
			if (type.getId() == typeId) {
				return type;
			}
		}
		return null;
	}

	public int getId() {
		return this.id;
	}

	public Class<? extends MGSInteropMessage> getMessageClass() {
		return this.messageClass;
	}
}
