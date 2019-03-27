package com.mercury.server.response;

import java.util.concurrent.atomic.AtomicInteger;

import com.mercury.server.response.impl.ExtensionResponse;
import com.mercury.server.response.impl.JoinRoomResponse;
import com.mercury.server.response.impl.LeaveRoomResponse;
import com.mercury.server.response.impl.LoginResponse;
import com.mercury.server.response.impl.LogoutResponse;
import com.mercury.server.response.impl.PingResponse;

public enum MGSResponseType {
	LOGIN(LoginResponse.class),
	LOGOUT(LogoutResponse.class),
	JOIN_ROOM(JoinRoomResponse.class),
	LEAVE_ROOM(LeaveRoomResponse.class),
	EXTENSION(ExtensionResponse.class),
	PING(PingResponse.class);

	private int id;
	private Class<? extends MGSResponse> messageClass;

	private MGSResponseType(Class<? extends MGSResponse> messageClass) {
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

	public Class<? extends MGSResponse> getResponseClass() {
		return this.messageClass;
	}

	public static MGSResponseType fromId(int id) {
		for (MGSResponseType type : values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		return null;
	}
}
