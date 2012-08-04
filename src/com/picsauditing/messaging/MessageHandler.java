package com.picsauditing.messaging;

import java.util.List;

import org.springframework.amqp.core.Message;

public interface MessageHandler {
	void handle(Message message);
	void handle(List<Message> messages);
}