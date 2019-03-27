package com.mercury.server.entity.room;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

class RoomManagerEventProducer {
	private final RingBuffer<RoomManagerEvent> ringBuffer;

	public RoomManagerEventProducer(RingBuffer<RoomManagerEvent> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}

	private static final EventTranslatorOneArg<RoomManagerEvent, RoomManagerEvent> TRANSLATOR = new EventTranslatorOneArg<RoomManagerEvent, RoomManagerEvent>() {
		public void translateTo(RoomManagerEvent event, long sequence, RoomManagerEvent from) {
			event.setEventType(from.getEventType());
			event.setRoom(from.getRoom());
			event.setRoomId(from.getRoomId());
			event.setSetting(from.getSetting());
		}
	};

	public void onData(RoomManagerEvent event) {
		ringBuffer.publishEvent(TRANSLATOR, event);
	}
}
