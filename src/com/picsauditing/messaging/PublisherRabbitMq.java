package com.picsauditing.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;


public class PublisherRabbitMq implements Publisher {
	@Autowired private RabbitTemplate amqpTemplate;
	
    public void setAmqpTemplate(RabbitTemplate amqpTemplate) {
    	this.amqpTemplate = amqpTemplate;
    }
    
	@Override
	public void publish(EnterpriseMessage message) {
		amqpTemplate.convertAndSend(message.getMessage());
	}

	public void publish(EnterpriseMessage message, String routingKey) {
		amqpTemplate.setRoutingKey(routingKey);
		amqpTemplate.convertAndSend(message);
	}

}
