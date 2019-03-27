package com.mercury.server.test;

import java.util.Map;

import com.mercury.server.annotation.AnnotationLoader;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.processor.ProcessorManager;

public class TestLoadProcessor {

	public static void main(String[] args) throws Exception {
		Map<MGSMessageType, Class<?>> messageRouting = AnnotationLoader.load("com.mercury.server.processor.impl");
		ProcessorManager manager = new ProcessorManager();
		manager.init(messageRouting, null);
	}
}
