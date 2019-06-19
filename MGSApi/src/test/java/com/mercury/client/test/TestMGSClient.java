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
		client.connect("LB-bogia-api-33867442.ap-southeast-1.elb.amazonaws.com", 9922);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				client.stop();
			}
		}));
	}
}
