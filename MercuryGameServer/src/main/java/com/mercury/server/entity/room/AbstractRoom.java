package com.mercury.server.entity.room;

import java.util.Map;
import java.util.Set;

import com.mercury.server.callback.RemoveRoomCallback;
import com.mercury.server.entity.user.User;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.MGSException;
import com.mercury.server.exception.data.ErrorCode;
import com.mercury.server.plugin.RoomPlugin;
import com.nhb.common.async.executor.DisruptorAsyncTaskExecutor;
import com.nhb.common.data.PuValue;
import com.nhb.eventdriven.impl.BaseEventDispatcher;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
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
	private DisruptorAsyncTaskExecutor executor;

	public abstract void joinRoom(User user) throws MGSException;

	public abstract void leaveRoom(User user, UserLeaveRoomReason reason, ErrorCode code) throws MGSException;

	public boolean hasPassword() {
		return hasPassword;
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

	public boolean isDestroy() {
		return isDestroy;
	}

	public void setDestroy(boolean isDestroy) {
		this.isDestroy = isDestroy;
	}
}
