package com.mercury.server.response.impl;

import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.response.MGSAbstractResponse;
import com.mercury.server.response.MGSResponseType;
import com.nhb.common.data.PuArray;

public class LogoutResponse extends MGSAbstractResponse {

	{
		setType(MGSResponseType.LOGOUT);
	}

	private boolean success;
	private UserDisconnectReason reason;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(success);
		array.addFrom(reason.getId());
	}

	@Override
	public void readPuArray(PuArray array) {
		this.success = array.remove(0).getBoolean();
		this.reason = UserDisconnectReason.fromId(array.remove(0).getInteger());
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public UserDisconnectReason getReason() {
		return reason;
	}

	public void setReason(UserDisconnectReason reason) {
		this.reason = reason;
	}

}
