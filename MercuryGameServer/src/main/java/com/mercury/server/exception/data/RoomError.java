package com.mercury.server.exception.data;

public enum RoomError implements ErrorCode {
	ROOM_IS_FULL(100, "Phòng đầy"),
	USER_ALREADY_IN_ROOM(101, "Bạn đã ở trong phòng"),
	ROOM_DOES_NOT_EXIT(102, "Phòng không tồn tại"),
	WRONG_PASSWORD(103, "Sai mật khẩu phòng"),
	ROOM_DESTROY(104, "Phòng đã bị hủy"),
	USER_NOT_IN_ROOM(105, "Bạn không có ở trong phòng"),
	USER_DISCONNECT(106, "Bạn bị mất kết nối");

	private int code;
	private String message = "";

	private RoomError(int code, String message) {
		this.code = code;
		this.message = message;
	}

	@Override
	public int getCode() {
		return this.code;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

}
