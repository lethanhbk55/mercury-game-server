package com.mercury.client;

import java.io.IOException;

import com.mercury.server.message.MGSAbstractMessage;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.MGSResponseHelper;
import com.nhb.common.data.PuElement;
import com.nhb.eventdriven.Event;
import com.nhb.eventdriven.EventHandler;
import com.nhb.eventdriven.impl.BaseEventDispatcher;
import com.nhb.eventdriven.impl.BaseEventHandler;
import com.nhb.messaging.TransportProtocol;
import com.nhb.messaging.socket.SocketEvent;
import com.nhb.messaging.socket.netty.NettySocketClient;

public abstract class MGSClient extends BaseEventDispatcher {

	private String zoneName;
	private NettySocketClient nettyClient;

	private EventHandler messageReceiveHandler = new BaseEventHandler(this, "onMessageReceiveHandler");
	private EventHandler openConnectionHandler = new BaseEventHandler(this, "onOpenConnectionHandler");
	private EventHandler closeConnectionHandler = new BaseEventHandler(this, "onCloseConnectionHandler");

	public MGSClient(String zoneName, TransportProtocol protocol) {
		this.zoneName = zoneName;
		nettyClient = new NettySocketClient();
		nettyClient.setProtocol(protocol);
		nettyClient.addEventListener(SocketEvent.MESSAGE, messageReceiveHandler);
		nettyClient.addEventListener(SocketEvent.CONNECTED, openConnectionHandler);
		nettyClient.addEventListener(SocketEvent.DISCONNECTED, closeConnectionHandler);
	}

	public final void connect(String host, int port) throws IOException {
		nettyClient.connect(host, port);
	}

	public final void close() throws Exception {
		this.nettyClient.close();
	}

	public final void send(MGSMessage message) {
		if (message instanceof MGSAbstractMessage) {
			((MGSAbstractMessage) message).setZoneName(zoneName);
		}
		this.nettyClient.send(message.serialize());
	}

	@Deprecated
	public void onOpenConnectionHandler(Event rawEvent) throws Exception {
		SocketEvent event = (SocketEvent) rawEvent;
		this.onConnectionSuccess(event);
	}

	@Deprecated
	public void onMessageReceiveHandler(Event rawEvent) throws Exception {
		SocketEvent event = (SocketEvent) rawEvent;
		PuElement data = event.getData();
		getLogger().debug("message receive: {}", data);
		MGSResponse response = MGSResponseHelper.deserilize(data);
		onMessageReceive(response);
	}

	@Deprecated
	public void onCloseConnectionHandler(Event rawEvent) {
		SocketEvent event = (SocketEvent) rawEvent;
		this.onConnectionClosed(event);
	}

	public abstract void onConnectionSuccess(SocketEvent event);

	public abstract void onConnectionClosed(SocketEvent event);

	public abstract void onMessageReceive(MGSResponse response);
}
