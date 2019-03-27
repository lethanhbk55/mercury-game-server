package com.mercury.server.entity.room;

import java.util.concurrent.atomic.AtomicInteger;

class RoomIdGeneratorImpl implements RoomIdGenerator {
	private AtomicInteger idSeed = new AtomicInteger();

	@Override
	public int nextId() {
		return idSeed.incrementAndGet();
	}

}
