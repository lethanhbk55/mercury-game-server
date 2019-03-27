package com.mercury.server.interop;

import com.mercury.server.interop.message.MGSInteropMessage;
import com.nhb.messaging.rabbit.producer.RabbitMQPubSubProducer;

public class MGSRabbitMQApi implements MGSServerApi {
	private RabbitMQPubSubProducer producer;

	public MGSRabbitMQApi(RabbitMQPubSubProducer producer) {
		this.producer = producer;
	}

	@Override
	public void call(MGSInteropMessage message) {
		this.producer.publish(message.serialize());
	}

}
