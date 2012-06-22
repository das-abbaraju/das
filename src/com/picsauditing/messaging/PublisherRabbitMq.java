package com.picsauditing.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.toggle.FeatureToggleChecker;

public class PublisherRabbitMq implements Publisher {
	@Autowired
	private RabbitTemplate amqpTemplate;
	@Autowired
	private FeatureToggleChecker featureToggleChecker;

	private final Logger logger = LoggerFactory.getLogger(PublisherRabbitMq.class);

	public void setAmqpTemplate(RabbitTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	@Override
	public void publish(Object message) {
		try {
			if (featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses")) {
				amqpTemplate.convertAndSend(message);
			}
			
		} catch (AmqpException amqpException) {
			logger.error(amqpException.getMessage(), amqpException);
		}
	}

	public void publish(Object message, String routingKey) {
		try {
			if (featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses")) {
				amqpTemplate.setRoutingKey(routingKey);
				amqpTemplate.convertAndSend(message);
			}

		} catch (AmqpException amqpException) {
			logger.error(amqpException.getMessage(), amqpException);
		}
	}

}
