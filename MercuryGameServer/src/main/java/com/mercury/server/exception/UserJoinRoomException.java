package com.mercury.server.exception;

import com.mercury.server.exception.data.ErrorCode;

public class UserJoinRoomException extends MGSException {

	private static final long serialVersionUID = 8204676589677355241L;

	public UserJoinRoomException(ErrorCode errorCode) {
		super(errorCode);
	}
}
