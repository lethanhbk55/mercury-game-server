package com.mercury.server.api.setting;

import java.util.Map;

import com.mercury.server.callback.CreateRoomCallback;
import com.mercury.server.callback.RemoveRoomCallback;
import com.mercury.server.entity.room.RoomRemoveMode;
import com.mercury.server.entity.user.User;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuValue;

public class CreateRoomSetting {

	private String roomName;
	private int maxUser;
	private boolean hasPassword;
	private String pluginClass;
	private CreateRoomCallback createRoomCallback;
	private RemoveRoomCallback removeRoomCallback;
	private PuObject initParams;
	private String password;
	private RoomRemoveMode removeMode;
	private User creator;
	private String groupId;
	private Map<String, PuValue> roomVariables;

	private CreateRoomSetting() {

	}

	public static CreateRoomSettingBuilder newBuilder() {
		return new CreateRoomSettingBuilder();
	}

	public static class CreateRoomSettingBuilder {
		private String roomName;
		private int maxUser;
		private boolean hasPassword;
		private String pluginClass;
		private PuObject initParams;
		private String password;
		private RoomRemoveMode removeMode;
		private User creator;
		private String groupId = "Default";
		private Map<String, PuValue> roomVariables;
		private CreateRoomCallback createRoomCallback;
		private RemoveRoomCallback removeRoomCallback;

		public CreateRoomSettingBuilder roomName(String roomName) {
			this.roomName = roomName;
			return this;
		}

		public CreateRoomSettingBuilder maxUser(int maxUser) {
			this.maxUser = maxUser;
			return this;
		}

		public CreateRoomSettingBuilder hasPassword(boolean hasPassword) {
			this.hasPassword = hasPassword;
			return this;
		}

		public CreateRoomSettingBuilder pluginClass(String pluginClass) {
			this.pluginClass = pluginClass;
			return this;
		}

		public CreateRoomSettingBuilder createRoomCallback(CreateRoomCallback callback) {
			this.createRoomCallback = callback;
			return this;
		}

		public CreateRoomSettingBuilder removeRoomCallback(RemoveRoomCallback callback) {
			this.removeRoomCallback = callback;
			return this;
		}

		public CreateRoomSettingBuilder initParams(PuObject initParams) {
			this.initParams = initParams;
			return this;
		}

		public CreateRoomSettingBuilder password(String password) {
			this.password = password;
			return this;
		}

		public CreateRoomSettingBuilder removeMode(RoomRemoveMode removeMode) {
			this.removeMode = removeMode;
			return this;
		}

		public CreateRoomSettingBuilder creator(User user) {
			this.creator = user;
			return this;
		}

		public CreateRoomSettingBuilder groupId(String groupId) {
			this.groupId = groupId;
			return this;
		}

		public CreateRoomSettingBuilder roomVariables(Map<String, PuValue> roomVariables) {
			this.roomVariables = roomVariables;
			return this;
		}

		public CreateRoomSetting build() {
			CreateRoomSetting setting = new CreateRoomSetting();
			setting.setRoomName(this.roomName);
			setting.setMaxUser(this.maxUser);
			setting.setCreator(this.creator);
			setting.setHasPassword(this.hasPassword);
			setting.setInitParams(this.initParams);
			setting.setPassword(this.password);
			setting.setPluginClass(this.pluginClass);
			setting.setRemoveMode(this.removeMode);
			setting.setCreateRoomCallback(this.createRoomCallback);
			setting.setGroupId(this.groupId);
			setting.setRoomVariables(roomVariables);
			setting.setRemoveRoomCallback(this.removeRoomCallback);
			return setting;
		}
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public int getMaxUser() {
		return maxUser;
	}

	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}

	public boolean isHasPassword() {
		return hasPassword;
	}

	public void setHasPassword(boolean hasPassword) {
		this.hasPassword = hasPassword;
	}

	public String getPluginClass() {
		return pluginClass;
	}

	public void setPluginClass(String pluginClass) {
		this.pluginClass = pluginClass;
	}

	public CreateRoomCallback getCreateRoomCallback() {
		return createRoomCallback;
	}

	public void setCreateRoomCallback(CreateRoomCallback callback) {
		this.createRoomCallback = callback;
	}

	public PuObject getInitParams() {
		return initParams;
	}

	public void setInitParams(PuObject initParams) {
		this.initParams = initParams;
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

	public Map<String, PuValue> getRoomVariables() {
		return roomVariables;
	}

	public void setRoomVariables(Map<String, PuValue> roomVariables) {
		this.roomVariables = roomVariables;
	}

	public RemoveRoomCallback getRemoveRoomCallback() {
		return removeRoomCallback;
	}

	public void setRemoveRoomCallback(RemoveRoomCallback removeRoomCallback) {
		this.removeRoomCallback = removeRoomCallback;
	}

}
