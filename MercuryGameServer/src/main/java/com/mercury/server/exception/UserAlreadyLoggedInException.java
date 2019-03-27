package com.mercury.server.exception;

import com.mercury.server.entity.user.User;

public class UserAlreadyLoggedInException extends Throwable {

	private static final long serialVersionUID = -7475802976023067260L;

	private User user;

	public UserAlreadyLoggedInException(String username) {
		super("user " + username + " already logged in");
	}

	public UserAlreadyLoggedInException(User user) {
		this(user.getUsername());
		this.setUser(user);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
