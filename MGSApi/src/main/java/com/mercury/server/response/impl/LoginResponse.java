package com.mercury.server.response.impl;

import com.mercury.server.response.MGSAbstractResponse;
import com.mercury.server.response.MGSResponseType;
import com.nhb.common.data.PuArray;

public class LoginResponse extends MGSAbstractResponse {

	{
		setType(MGSResponseType.LOGIN);
	}

	private boolean success;
	private int errorCode;
	private String username;
	private String zoneName;
	private String message;

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(success);
		array.addFrom(errorCode);
		array.addFrom(username);
		array.addFrom(zoneName);
		array.addFrom(message);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.success = array.remove(0).getBoolean();
		this.errorCode = array.remove(0).getInteger();
		this.username = array.remove(0).getString();
		this.zoneName = array.remove(0).getString();
		this.message = array.remove(0).getString();
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
