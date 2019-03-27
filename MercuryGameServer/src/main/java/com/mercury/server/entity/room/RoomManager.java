package com.mercury.server.entity.room;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mario.gateway.socket.SocketSession;
import com.mario.schedule.ScheduledCallback;
import com.mercury.server.api.setting.CreateRoomSetting;
import com.mercury.server.callback.CreateRoomCallback;
import com.mercury.server.callback.RemoveRoomCallback;
import com.nhb.common.BaseLoggable;
import com.nhb.common.data.PuObject;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;
import com.nhb.eventdriven.impl.BaseEventHandler;

public class RoomManager extends BaseLoggable {

	private Map<Integer, Room> rooms;
	private Map<String, List<Room>> roomGroups;
	private RoomIdGenerator idGenerator;

	private EventHandler userJoinRoomHanlder = new BaseEventHandler(this, "onUserJoinRoomHandler");
	private EventHandler userLeaveRoomHanlder = new BaseEventHandler(this, "onUserLeaveRoomHandler");

	public RoomManager() {
		rooms = new ConcurrentHashMap<>();
		roomGroups = new ConcurrentHashMap<>();
		idGenerator = new RoomIdGeneratorImpl();
	}

	public void addRoom(Room room, CreateRoomSetting setting) {
		int roomId = idGenerator.nextId();
		if (room instanceof AbstractRoom) {
			((AbstractRoom) room).setRoomId(roomId);
		}
		rooms.put(roomId, room);

		synchronized (this.roomGroups) {
			if (!roomGroups.containsKey(room.getGroupId())) {
				roomGroups.put(room.getGroupId(), new ArrayList<>());
			}
		}

		List<Room> roomList = roomGroups.get(room.getGroupId());

		synchronized (roomList) {
			roomList.add(room);
		}

		getLogger().info("Room created: roomId {} - roomName {} - groupId: {}", roomId, room.getRoomName(),
				room.getGroupId());

		if (room instanceof AbstractRoom) {
			AbstractRoom roomImpl = (AbstractRoom) room;
			roomImpl.addEventListener(RoomEvent.JOIN, userJoinRoomHanlder);
			roomImpl.addEventListener(RoomEvent.LEAVE, userLeaveRoomHanlder);
			roomImpl.setRemoveRoomCallback(setting.getRemoveRoomCallback());
		}

		PuObject initParams = setting.getInitParams();
		room.getRoomPlugin().init(initParams);

		CreateRoomCallback callback = setting.getCreateRoomCallback();
		if (callback != null) {
			room.getRoomPlugin().getPluginApi().execute(new ScheduledCallback() {

				@Override
				public void call() {
					try {
						callback.call(room);
					} catch (Exception e) {
						getLogger().error("create room callback error", e);
					}
				}
			});
		}
	}

	public void removeRoom(int roomId) {
		Room room = rooms.get(roomId);
		if (room != null) {
			removeRoom(room);
		} else {
			getLogger().warn("Can't remove requested room. ID = {}. Room was not found", roomId);
		}
	}

	private void removeRoom(Room room) {

		synchronized (room) {
			if (room.getUsers().size() == 0) {

				if (room instanceof AbstractRoom) {
					((AbstractRoom) room).setDestroy(true);
				}

			}
		}

		if (room.isDestroy()) {
			rooms.remove(room.getRoomId());

			List<Room> roomList = roomGroups.get(room.getGroupId());
			if (roomList != null) {
				synchronized (roomList) {
					roomList.remove(room);
				}
			}

			getLogger().info("Room {} has been removed", room.getRoomId());
			if (room instanceof AbstractRoom) {
				((AbstractRoom) room).removeAllEventListener();
				RemoveRoomCallback removeRoomCallback = ((AbstractRoom) room).getRemoveRoomCallback();
				if (removeRoomCallback != null) {
					room.getRoomPlugin().getPluginApi().execute(new ScheduledCallback() {

						@Override
						public void call() {
							try {
								removeRoomCallback.call(room);
							} catch (Exception e) {
								getLogger().debug("remove room callback exception", e);
							}
						}
					});
				}
			}
		}
	}

	@Deprecated
	public void onUserJoinRoomHandler(Event rawEvent) {

	}

	@Deprecated
	public void onUserLeaveRoomHandler(Event rawEvent) {
		RoomEvent event = (RoomEvent) rawEvent;
		Room room = event.getRoom();
		switch (room.getRemoveMode()) {
		case WHEN_EMPTY_AND_CREATOR_IS_GONE: {
			if (room.getCreator() != null && room.size() == 0) {
				SocketSession socketSession = room.getRoomPlugin().getMarioApi()
						.getSocketSession(room.getCreator().getSessionId());
				if (socketSession == null) {
					this.removeRoom(room.getRoomId());
				}
			}
			break;
		}
		case WHEN_EMPTY: {
			if (room.size() == 0) {
				this.removeRoom(room.getRoomId());
			}
			break;
		}
		default:
			break;
		}
	}

	public void stop() {
		for (Room room : this.rooms.values()) {
			try {
				room.getRoomPlugin().destroy();
			} catch (Exception e) {
				System.out.println("destroy room plugin get ex " + e);
			}
		}
	}

	public Room findRoomById(int roomId) {
		return this.rooms.get(roomId);
	}

	public Collection<Room> getRooms() {
		return this.rooms.values();
	}

	public Set<String> getGroups() {
		return this.roomGroups.keySet();
	}

	public List<Room> getRoomsByGroupId(String groupId) {
		return new ArrayList<>(this.roomGroups.get(groupId));
	}

	public int size() {
		return this.rooms.size();
	}
}
