package com.mercury.server.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.mercury.server.MGSHandler;
import com.mercury.server.exception.MessageTypeNotHandling;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.response.MGSResponse;

public class ProcessorManager {

	private Map<MGSMessageType, MGSProcessor> processors;

	public ProcessorManager() {
		processors = new HashMap<MGSMessageType, MGSProcessor>();
	}

	public void init(Map<MGSMessageType, Class<?>> messageRouting, MGSHandler handler) throws Exception {
		for (Entry<MGSMessageType, Class<?>> entry : messageRouting.entrySet()) {
			MGSProcessor processor = (MGSProcessor) this.getClass().getClassLoader()
					.loadClass(entry.getValue().getName()).newInstance();
			if (processor instanceof MGSAbstractProcessor) {
				((MGSAbstractProcessor) processor).setContext(handler);
			}
			processors.put(entry.getKey(), processor);
		}
	}

	public MGSResponse processCommand(String sessionId, MGSMessage message)
			throws MessageTypeNotHandling, ProcessMessageException {
		if (this.processors.containsKey(message.getType())) {
			return this.processors.get(message.getType()).execute(sessionId, message);
		} else {
			throw new MessageTypeNotHandling();
		}
	}
}
