package com.mercury.server.entity.session;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

class PingEventProducer {
	private final RingBuffer<PingEvent> ringBuffer;

	public PingEventProducer(RingBuffer<PingEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	private static final EventTranslatorOneArg<PingEvent, PingEvent> TRANSLATOR = new EventTranslatorOneArg<PingEvent, PingEvent>() {
		public void translateTo(PingEvent event, long sequence, PingEvent from) {
			event.setSessionId(from.getSessionId());
			event.setSessionsToRemove(from.getSessionsToRemove());
			event.setType(from.getType());
		}
	};

	public void onData(PingEvent event) {
		ringBuffer.publishEvent(TRANSLATOR, event);
	}
}
