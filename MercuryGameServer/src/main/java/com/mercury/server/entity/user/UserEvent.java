package com.mercury.server.entity.user;

import com.mercury.server.event.reason.UserDisconnectReason;
import com.nhb.eventdriven.Callable;
import com.nhb.eventdriven.impl.AbstractEvent;

public class UserEvent extends AbstractEvent {

	public static final String ADD = "add";
	public static final String REMOVE = "remove";

	public UserEvent(String type) {
		setType(type);
	}

	public UserEvent(String type, User user) {
		this(type);
		setUser(user);
	}

	public UserEvent(String type, User user, UserDisconnectReason reason) {
		this(type, user);
		setReason(reason);
	}

	public UserEvent(String type, User user, UserDisconnectReason reason, Callable callback) {
		this(type, user, reason);
		setCallback(callback);
	}

	private User user;
	private UserDisconnectReason reason;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserDisconnectReason getReason() {
		return reason;
	}

	public void setReason(UserDisconnectReason reason) {
		this.reason = reason;
	}
}
