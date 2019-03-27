package com.mercury.client.test;

import java.io.IOException;

import com.nhb.common.BaseLoggable;
import com.nhb.common.utils.Initializer;
import com.nhb.messaging.TransportProtocol;

public class TestMGSClient extends BaseLoggable {
	static {
		Initializer.bootstrap(TestMGSClient.class);
	}

	public static void main(String[] args) throws IOException {
		System.out.println("Open connect to MiniGame Server");
		final BaseMGSClient client = new BaseMGSClient("MiniGame", TransportProtocol.TCP);
		// client.connect(args[0], Integer.parseInt(args[1]));
		client.connect("api.dev.bogia", 9922);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				client.stop();
			}
		}));
	}
}
