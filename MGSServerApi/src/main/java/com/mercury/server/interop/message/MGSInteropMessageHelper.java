package com.mercury.server.interop.message;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;

public class MGSInteropMessageHelper {

	public static MGSInteropMessage deserialize(PuElement request) throws Exception {
		if (request instanceof PuArray) {
			PuArray array = (PuArray) request;
			int typeId = array.remove(0).getInteger();
			String zoneName = array.remove(0).getString();
			MGSInteropMessageType type = MGSInteropMessageType.fromId(typeId);
			MGSInteropMessage message = type.getMessageClass().newInstance();
			if (message instanceof MGSAbstractInteropMessage) {
				MGSAbstractInteropMessage abstractMessage = (MGSAbstractInteropMessage) message;
				abstractMessage.setType(type);
				abstractMessage.setZoneName(zoneName);
				abstractMessage.readPuArray(array);
			}
			return message;
		}
		return null;
	}
}
