package com.mercury.server.entity.user;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.UserAlreadyLoggedInException;
import com.nhb.eventdriven.Callable;
import com.nhb.eventdriven.impl.BaseEventDispatcher;

public final class UserManager extends BaseEventDispatcher {

	private Map<String, User> users;
	private Map<String, User> userBySessionIds;

	public UserManager() {
		this.users = new ConcurrentHashMap<String, User>();
		this.userBySessionIds = new ConcurrentHashMap<>();
	}

	public void addUser(User user) throws UserAlreadyLoggedInException {
		if (users.containsKey(user.getUsername())) {
			throw new UserAlreadyLoggedInException(users.get(user.getUsername()));
		}
		try {
			users.put(user.getUsername(), user);
			userBySessionIds.put(user.getSessionId(), user);
		} finally {
			UserManager.this.dispatchEvent(new UserEvent(UserEvent.ADD, user));
		}
	}

	public void removeUser(String username, UserDisconnectReason reason, Callable callable) {
		if (username != null && users.containsKey(username)) {
			User user = null;
			try {
				user = users.remove(username);
				userBySessionIds.remove(user.getSessionId());
			} finally {
				UserManager.this.dispatchEvent(new UserEvent(UserEvent.REMOVE, user, reason, callable));
			}
		}
	}

	public void removeUser(String username, UserDisconnectReason reason) {
		if (username != null && users.containsKey(username)) {
			User user = null;
			try {
				user = users.remove(username);
				if (user != null) {
					userBySessionIds.remove(user.getSessionId());
				}
			} finally {
				UserManager.this.dispatchEvent(new UserEvent(UserEvent.REMOVE, user, reason));
			}
		}
	}

	public boolean removeUserBySessionId(String sessionId, UserDisconnectReason reason) {
		User user = this.userBySessionIds.get(sessionId);
		if (user != null) {
			this.removeUser(user.getUsername(), reason);
			return true;
		}
		return false;
	}

	public Collection<User> getUsers() {
		return this.users.values();
	}

	public void stop() {

	}

	public User getByUsername(String username) {
		return this.users.get(username);
	}

	public User getBySessionId(String sessionId) {
		return this.userBySessionIds.get(sessionId);
	}

	public int size() {
		return users.size();
	}
}
