package com.picsauditing.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.picsauditing.toggle.FeatureToggle;

public class PublisherRabbitMq implements Publisher {
	private final Logger logger = LoggerFactory.getLogger(PublisherRabbitMq.class);
	
	private RabbitTemplate amqpTemplate;
	private FeatureToggle featureToggleChecker;
	private String featureToggleName = FeatureToggle.TOGGLE_BPROC;

	public void setAmqpTemplate(RabbitTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void setFeatureToggleChecker(FeatureToggle featureToggleChecker) {
		this.featureToggleChecker = featureToggleChecker;
	}
	
	public void setFeatureToggleName(String featureToggleName) {
		this.featureToggleName = featureToggleName;
	}
	
	@Override
	public void publish(Object message) {
			if (featureToggleChecker == null || featureToggleChecker.isFeatureEnabled(featureToggleName)) {
				amqpTemplate.convertAndSend(message);
			}
	}

	public void publish(Object message, String routingKey) {
			if (featureToggleChecker.isFeatureEnabled(featureToggleName)) {
				amqpTemplate.convertAndSend(routingKey, message);
			}
	}

}
