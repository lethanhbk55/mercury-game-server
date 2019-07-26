package com.mercury.server.api;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.util.ConcurrentHashSet;

import com.mercury.server.callback.JoinRoomCallback;
import com.mercury.server.entity.room.AbstractRoom;
import com.mercury.server.entity.room.Room;
import com.mercury.server.entity.room.RoomMessenger;
import com.mercury.server.entity.room.RoomRemoveMode;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.user.UserImpl;
import com.mercury.server.event.reason.UserLeaveRoomReason;
import com.mercury.server.exception.MGSException;
import com.mercury.server.exception.UserJoinRoomException;
import com.mercury.server.exception.UserLeaveRoomException;
import com.mercury.server.exception.data.ErrorCode;
import com.mercury.server.exception.data.RoomError;

class RoomImpl extends AbstractRoom implements Room {
	private RoomMessenger messenger;

	public RoomImpl() {
		setRoomVariables(new ConcurrentHashMap<>());
		setUsers(new ConcurrentHashSet<>());
		setRemoveMode(RoomRemoveMode.DEFAULT);
	}

	public void joinRoom(User user, JoinRoomCallback callback) {
		getExecutor().execute(new Runnable() {

			@Override
			public void run() {
				try {
					_joinRoom(user);
				} catch (MGSException e) {
					try {
						getMessenger().sendJoinRoomFail(user, getRoomId(), e.getErrorCode(), callback);
					} catch (Exception e1) {
						getLogger().error("send join room fail has exception", e1);
					}
				} catch (Exception e) {
					getLogger().error("user join room error", e);
				}
			}
		});
	}

	@Override
	public void leaveRoom(User user, UserLeaveRoomReason reason, ErrorCode code) {
		getExecutor().execute(new Runnable() {

			@Override
			public void run() {
				try {
					_leaveRoom(user, reason, code);
				} catch (MGSException e) {
					try {
						getMessenger().sendLeaveRoomFail(user, getRoomId(), e.getErrorCode());
					} catch (Exception e1) {
						getLogger().error("send leave room fail has exception", e1);
					}
				} catch (Exception e) {
					getLogger().error("user leave room error", e);
				}
			}
		});
	}

	private void _joinRoom(User user) throws MGSException {
		if (this.getUsers().contains(user)) {
			throw new UserJoinRoomException(RoomError.USER_ALREADY_IN_ROOM);
		}

		if (this.isDestroy()) {
			throw new UserJoinRoomException(RoomError.ROOM_DESTROY);
		}

		if (this.getUsers().size() >= getMaxUser()) {
			throw new UserJoinRoomException(RoomError.ROOM_IS_FULL);
		}

		this.getRoomPlugin().userEnter(user);
		this.getUsers().add(user);
		if (user instanceof UserImpl) {
			((UserImpl) user).setLastJoinedRoom(this);
		}

		getLogger().info("User {} has joined room {}", user.getUsername(), getRoomId());
		getMessenger().sendJoinRoomSuccess(user, getRoomId());
		RoomImpl.this.getRoomPlugin().userDidEnter(user);
	}

	private void _leaveRoom(User user, UserLeaveRoomReason reason, ErrorCode code) throws MGSException {
		if (!this.getUsers().contains(user)) {
			throw new UserLeaveRoomException(RoomError.USER_NOT_IN_ROOM);
		}

		this.getUsers().remove(user);
		if (user instanceof UserImpl) {
			((UserImpl) user).setLastJoinedRoom(null);
		}
		this.getRoomPlugin().userExit(user);

		getLogger().info("User {} has left room {}", user.getUsername(), getRoomId());

		this.getMessenger().sendLeaveRoomSuccess(user, getRoomId(), reason, code);
		RoomImpl.this.getRoomPlugin().userDidExit(user);
	}

	@Override
	public int size() {
		return this.getUsers().size();
	}

	public RoomMessenger getMessenger() {
		return messenger;
	}

	public void setMessenger(RoomMessenger messenger) {
		this.messenger = messenger;
	}

}
