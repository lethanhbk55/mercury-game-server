package com.mercury.server.entity.session;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

class SessionEventProducer {
	private final RingBuffer<SessionEvent> ringBuffer;

	public SessionEventProducer(RingBuffer<SessionEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	private static final EventTranslatorOneArg<SessionEvent, SessionEvent> TRANSLATOR = new EventTranslatorOneArg<SessionEvent, SessionEvent>() {
		public void translateTo(SessionEvent event, long sequence, SessionEvent from) {
			event.setEventType(from.getEventType());
			event.setSessionId(from.getSessionId());
			event.setZone(from.getZone());
		}
	};

	public void onData(SessionEvent event) {
		ringBuffer.publishEvent(TRANSLATOR, event);
	}
}
