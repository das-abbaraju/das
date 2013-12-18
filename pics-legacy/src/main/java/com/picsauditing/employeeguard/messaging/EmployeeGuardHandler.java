package com.picsauditing.employeeguard.messaging;

import com.picsauditing.messaging.MessageHandler;
import org.springframework.amqp.core.Message;

import java.util.List;

public class EmployeeGuardHandler implements MessageHandler {
	@Override
	public void handle(List<Message> messages) {
		for (Message message : messages) {
			handle(message);
		}
	}

	@Override
	public void handle(Message message) {
		try {
			String msgBody = new String(message.getBody());
		} catch (Exception e) {

		}
	}

}
