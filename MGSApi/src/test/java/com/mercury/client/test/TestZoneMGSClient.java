package com.mercury.client.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mercury.client.MGSClient;
import com.mercury.server.message.PingMessage;
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

public class TestZoneMGSClient extends MGSClient {
	private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

	public TestZoneMGSClient(String zoneName, TransportProtocol protocol) {
		super(zoneName, protocol);
	}

	@Override
	public void onConnectionSuccess(SocketEvent event) {
		getLogger().debug("connection success");
		System.out.println("Connection Success");
		sendLoginMessage();
		sendPing();
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
		this.send(new LoginMessage("test", "password"));
	}

	@Override
	public void onConnectionClosed(SocketEvent event) {

	}

	@Override
	public void onMessageReceive(MGSResponse response) {
		getLogger().debug("message receive, type: {}", response.getType());
		System.out.println(String.format("Message receive, type: %s", response.getType()));
		if (response instanceof LoginResponse) {
			LoginResponse resp = (LoginResponse) response;
			System.out.println("Login success: " + resp.isSuccess());
			if (resp.isSuccess()) {
				hello();
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

	private void hello() {
		PuObject params = new PuObject();
		params.setString("command", "hello");
		params.setString("name", "baymet");
		this.send(new ZonePluginMessage("helloPlugin", params));
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
