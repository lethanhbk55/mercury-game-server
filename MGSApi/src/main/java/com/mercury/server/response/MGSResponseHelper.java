package com.mercury.server.response;

import com.nhb.common.data.PuArray;
import com.nhb.common.data.PuElement;

public class MGSResponseHelper {

	public static MGSResponse deserilize(PuElement data) throws Exception {
		if (data instanceof PuArray) {
			PuArray array = (PuArray) data;
			int messageId = array.remove(0).getInteger();
			int typeId = array.remove(0).getInteger();
			MGSResponseType type = MGSResponseType.fromId(typeId);
			MGSResponse message = type.getResponseClass().newInstance();
			
			if (message instanceof MGSAbstractResponse) {
				MGSAbstractResponse abstractMessage = (MGSAbstractResponse) message;
				abstractMessage.setMessageId(messageId);
				abstractMessage.setType(type);
				abstractMessage.readPuArray(array);
			}

			return message;
		}

		return null;
	}
}
