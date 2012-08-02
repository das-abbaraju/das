package com.picsauditing.messaging;

public interface Publisher {
	void publish(Object message);
	void publish(Object message, String routingKey);
}
