package com.picsauditing.employeeguard.messaging;

import com.picsauditing.messaging.MessageHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class EmployeeGuardListener implements MessageListener {
	@Autowired
	@Qualifier("employeeGuardHandler")
	private MessageHandler employeeGuardHandler;

	@Override
	public void onMessage(Message message) {
		employeeGuardHandler.handle(message);
	}
}
