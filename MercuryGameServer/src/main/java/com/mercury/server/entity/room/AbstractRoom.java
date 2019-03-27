package com.mercury.server.entity.room;

import java.util.Map;
import java.util.Set;

import com.mercury.server.callback.RemoveRoomCallback;
import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.MGSException;
import com.mercury.server.exception.data.ErrorCode;
import com.mercury.server.plugin.RoomPlugin;
import com.nhb.common.data.PuValue;
import com.nhb.eventdriven.impl.BaseEventDispatcher;

public abstract class AbstractRoom extends BaseEventDispatcher implements Room {

	private int roomId;
	private String roomName;
	private Map<String, PuValue> roomVariables;
	private RoomPlugin roomPlugin;
	private Set<User> users;
	private int maxUser;
	private boolean hasPassword;
	private String password;
	private RoomRemoveMode removeMode;
	private User creator;
	private String groupId;
	private RemoveRoomCallback removeRoomCallback;
	private boolean isDestroy;

	public abstract void joinRoom(User user) throws MGSException;

	public abstract void leaveRoom(User user, UserLeaveRoomReason reason, ErrorCode code) throws MGSException;

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Map<String, PuValue> getRoomVariables() {
		return roomVariables;
	}

	public void setRoomVariables(Map<String, PuValue> roomVariables) {
		this.roomVariables = roomVariables;
	}

	public RoomPlugin getRoomPlugin() {
		return roomPlugin;
	}

	public void setRoomPlugin(RoomPlugin roomPlugin) {
		this.roomPlugin = roomPlugin;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public int getMaxUser() {
		return maxUser;
	}

	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}

	public boolean hasPassword() {
		return hasPassword;
	}

	public void setHasPassword(boolean hasPassword) {
		this.hasPassword = hasPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RoomRemoveMode getRemoveMode() {
		return removeMode;
	}

	public void setRemoveMode(RoomRemoveMode removeMode) {
		this.removeMode = removeMode;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public PuValue getRoomVariable(String key) {
		return this.roomVariables.get(key);
	}

	@Override
	public boolean variableExists(String key) {
		return this.roomVariables.containsKey(key);
	}

	public void setRoomVariable(String key, PuValue value) {
		if (this.roomVariables != null) {
			this.roomVariables.put(key, value);
		}
	}

	public RemoveRoomCallback getRemoveRoomCallback() {
		return removeRoomCallback;
	}

	public void setRemoveRoomCallback(RemoveRoomCallback removeRoomCallback) {
		this.removeRoomCallback = removeRoomCallback;
	}

	public boolean isDestroy() {
		return isDestroy;
	}

	public void setDestroy(boolean isDestroy) {
		this.isDestroy = isDestroy;
	}
}
