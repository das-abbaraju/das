package com.picsauditing.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class PublisherRabbitMq implements Publisher {
	@Autowired private RabbitTemplate amqpTemplate;
	
    public void setAmqpTemplate(RabbitTemplate amqpTemplate) {
    	this.amqpTemplate = amqpTemplate;
    }
    
	@Override
	public void publish(String message) {
		amqpTemplate.convertAndSend(message);
	}

	public void publish(String message, String routingKey) {
		amqpTemplate.setRoutingKey(routingKey);
		amqpTemplate.convertAndSend(message);
	}
}
