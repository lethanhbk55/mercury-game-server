package com.mercury.client.test;

import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.response.impl.LeaveRoomResponse;

public class TestEnum {
	public static void main(String[] args) {
		LeaveRoomResponse reponse = new LeaveRoomResponse();
		reponse.setReason(UserLeaveRoomReason.KICKED);
		reponse.setSuccess(true);
		reponse.setRoomId(1);
		System.out.println(reponse.serialize());
	}
}
