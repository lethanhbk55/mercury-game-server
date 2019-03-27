package com.mercury.server.schedule;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

class MGSEventProducer {
	private final RingBuffer<MGSEvent> ringBuffer;

	public MGSEventProducer(RingBuffer<MGSEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	private static final EventTranslatorOneArg<MGSEvent, MGSEvent> TRANSLATOR = new EventTranslatorOneArg<MGSEvent, MGSEvent>() {
		public void translateTo(MGSEvent event, long sequence, MGSEvent from) {
			event.setCallback(from.getCallback());
		}
	};

	public void onData(MGSEvent event) {
		ringBuffer.publishEvent(TRANSLATOR, event);
	}
}
