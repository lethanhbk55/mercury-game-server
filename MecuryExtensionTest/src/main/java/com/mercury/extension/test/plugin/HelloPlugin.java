package com.mercury.extension.test.plugin;

import com.mercury.server.entity.user.User;
import com.mercury.server.plugin.impl.MGSPlugin;
import com.nhb.common.data.PuObject;
import com.nhb.common.data.PuObjectRO;

public class HelloPlugin extends MGSPlugin {

	@Override
	public void request(User user, PuObject params) {
		String command = params.getString("command");
		switch (command) {
		case "hello":
			String name = params.getString("name");
			PuObject message = new PuObject();
			message.setString("command", command);
			message.setString("say", "hello " + name);
			getPluginApi().sendMessageToUser(user.getUsername(), message);
			break;

		default:
			break;
		}
	}

	@Override
	public void init(PuObjectRO initParams) {
		getLogger().debug("init hello plugin with params: {}", initParams);
	}

}
