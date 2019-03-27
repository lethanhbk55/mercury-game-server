package com.mercury.server.entity.user;

import com.mercury.server.event.reason.UserDisconnectReason;
import com.nhb.eventdriven.Callable;

class UserManagerEvent {

	private EventType eventType;
	private String username;
	private User user;
	private UserDisconnectReason reason;
	private Callable callable;

	static enum EventType {
		ADD,
		REMOVE;
	}

	public UserManagerEvent() {

	}

	public UserManagerEvent(EventType eventType, String username) {
		this();
		setEventType(eventType);
		setUsername(username);
	}

	public UserManagerEvent(EventType eventType, User user) {
		setEventType(eventType);
		setUser(user);
	}

	public UserManagerEvent(EventType eventType, String username, UserDisconnectReason reason) {
		this(eventType, username);
		setReason(reason);
	}

	public UserManagerEvent(EventType eventType, String username, UserDisconnectReason reason, Callable callable) {
		this(eventType, username, reason);
		setCallable(callable);
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

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

	public Callable getCallable() {
		return callable;
	}

	public void setCallable(Callable callable) {
		this.callable = callable;
	}

}
