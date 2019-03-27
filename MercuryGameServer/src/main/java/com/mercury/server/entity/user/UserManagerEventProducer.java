package com.mercury.server.entity.user;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class UserManagerEventProducer {

	private final RingBuffer<UserManagerEvent> ringBuffer;

	public UserManagerEventProducer(RingBuffer<UserManagerEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	private static final EventTranslatorOneArg<UserManagerEvent, UserManagerEvent> TRANSLATOR = new EventTranslatorOneArg<UserManagerEvent, UserManagerEvent>() {
		public void translateTo(UserManagerEvent event, long sequence, UserManagerEvent from) {
			event.setCallable(from.getCallable());
			event.setEventType(from.getEventType());
			event.setUser(from.getUser());
			event.setReason(from.getReason());
			event.setUsername(from.getUsername());
		}
	};

	public void onData(UserManagerEvent event) {
		ringBuffer.publishEvent(TRANSLATOR, event);
	}
}
