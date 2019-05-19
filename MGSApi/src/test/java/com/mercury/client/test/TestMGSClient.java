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
		final BaseMGSClient client = new BaseMGSClient("Simms", TransportProtocol.TCP);
		// client.connect(args[0], Integer.parseInt(args[1]));
		client.connect("dev.261e825f609d4a7c0bd39cd49db561b3.com", 9923);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				client.stop();
			}
		}));
	}
}
