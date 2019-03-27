package com.mercury.server.message.impl;

import com.mercury.server.message.MGSAbstractMessage;
import com.mercury.server.message.MGSMessageType;
import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuObject;

public class LoginMessage extends MGSAbstractMessage {

	{
		setType(MGSMessageType.LOGIN);
	}

	private String username;
	private String password;
	private PuObject params;
	
	public LoginMessage() {
		
	}

	public LoginMessage(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	public LoginMessage(String username, String password, PuObject params) {
		this(username, password);
		this.params = params;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PuObject getParams() {
		return params;
	}

	public void setParams(PuObject params) {
		this.params = params;
	}

	@Override
	protected void writePuArray(PuArray array) {
		array.addFrom(username);
		array.addFrom(password);
		array.addFrom(params);
	}

	@Override
	public void readPuArray(PuArray array) {
		this.username = array.remove(0).getString();
		this.password = array.remove(0).getString();
		this.params = array.remove(0).getPuObject();
	}

}
