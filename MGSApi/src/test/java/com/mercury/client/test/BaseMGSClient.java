package com.mercury.client.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mercury.client.MGSClient;
import com.mercury.server.message.PingMessage;
import com.mercury.server.message.impl.JoinRoomMessage;
import com.mercury.server.message.impl.LoginMessage;
import com.mercury.server.message.impl.ZonePluginMessage;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.impl.ExtensionResponse;
import com.mercury.server.response.impl.JoinRoomResponse;
import com.mercury.server.response.impl.LoginResponse;
import com.mercury.server.response.impl.PingResponse;
import com.nhb.common.data.PuObject;
import com.nhb.messaging.TransportProtocol;
import com.nhb.messaging.socket.SocketEvent;

public class BaseMGSClient extends MGSClient {
	private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	public BaseMGSClient(String zoneName, TransportProtocol protocol) {
		super(zoneName, protocol);
	}

	@Override
	public void onConnectionSuccess(SocketEvent event) {
		getLogger().debug("connection success");
		System.out.println("Connection Success");
		sendLoginMessage();
		sendPing();
	}

	@Override
	public void onConnectionClosed(SocketEvent event) {
		getLogger().debug("connection close");
		System.out.println("Connection Close");
		stop();
	}

	@Override
	public void onMessageReceive(MGSResponse response) {
		getLogger().debug("message receive, type: {}", response.getType());
		System.out.println(String.format("Message receive, type: %s", response.getType()));
		if (response instanceof LoginResponse) {
			LoginResponse resp = (LoginResponse) response;
			System.out.println("Login success: " + resp.isSuccess());
			if (resp.isSuccess()) {
				// sendSubscribleGameId();
			}
		} else if (response instanceof JoinRoomResponse) {
			JoinRoomResponse joinRoomResponse = (JoinRoomResponse) response;
			int success = joinRoomResponse.getErrorCode();
			getLogger().debug("join room status: {}, errorCode: {}", joinRoomResponse.isSuccess(), success);
		} else if (response instanceof ExtensionResponse) {
			getLogger().debug("data: {}", ((ExtensionResponse) response).getData());
			System.out.println(String.format("Extension Response: %s", ((ExtensionResponse) response).getData()));
		} else if (response instanceof PingResponse) {
			PingResponse ping = (PingResponse) response;
			getLogger().debug("ping: {} ms", System.currentTimeMillis() - ping.getTimestamp());
			System.out.println(String.format("Ping: %s ms", System.currentTimeMillis() - ping.getTimestamp()));
		}
	}

	public void sendSubscribleGameId() {
		ZonePluginMessage message = new ZonePluginMessage();
		message.setPluginName("channelPlugin");
		PuObject params = new PuObject();
		params.setInteger("cmd", 300);
		params.setInteger("gid", 1);
		message.setParams(params);
		this.send(message);
	}

	public void sendJoinRoom() {
		JoinRoomMessage joinRoomMessage = new JoinRoomMessage(1, "");
		this.send(joinRoomMessage);
	}

	private void sendPing() {
		service.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				PingMessage pingMessage = new PingMessage();
				send(pingMessage);
			}
		}, 5, 5, TimeUnit.SECONDS);
	}

	private void sendLoginMessage() {
		PuObject info = new PuObject();
		info.setBoolean("isMerchant", false);
		info.setString("userId", "e2308a19-4aee-425d-93fa-c24568a2e137");
		info.setString("username", "seven1");
		info.setLong("timestamp", 1544411936178L);
		PuObject params = new PuObject();
		params.setString("signature",
				"0CB7C74A709828BA36F566AB2938962E52B39BBB4CEACB9DAB6D2F07DA7F7E433BA6DE28440BB22C9B6A9875CAF457479A173985275D021643305E4C5DF15538F939425D07B21AA17FFFA4F96DA9238F55FBFF2DC1C120DC4474504815180B9724AF0CA39D9A04E2115612B4610A2C991FA02D697DD163BE5FEE0AE260B1FA72");
		params.setString("info", info.toJSON());
		LoginMessage loginMessage = new LoginMessage("", null, params);
		this.send(loginMessage);
	}

	public void stop() {
		service.shutdown();
		try {
			if (service.awaitTermination(3, TimeUnit.SECONDS)) {
				service.shutdownNow();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
