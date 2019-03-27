package com.mercury.server.message;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;

public class MGSMessageHelper {

	public static MGSMessage deserilize(PuElement data) throws Exception {
		if (data instanceof PuArray) {
			PuArray array = (PuArray) data;
			int messageId = array.remove(0).getInteger();
			String zoneName = array.remove(0).getString();

			MGSMessageType type = MGSMessageType.fromId(messageId);
			MGSMessage message = type.getMessageClass().newInstance();
			if (message instanceof MGSAbstractMessage) {
				MGSAbstractMessage abstractMessage = (MGSAbstractMessage) message;
				abstractMessage.setType(type);
				abstractMessage.setZoneName(zoneName);
				abstractMessage.readPuArray(array);
			}

			return message;
		}

		return null;
	}
}
