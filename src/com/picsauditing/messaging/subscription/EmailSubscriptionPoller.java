package com.picsauditing.messaging.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import com.picsauditing.messaging.MessageHandler;
import com.picsauditing.toggle.FeatureToggleChecker;

public class EmailSubscriptionPoller {
	private final Logger logger = LoggerFactory.getLogger(EmailSubscriptionPoller.class);
	
	private RabbitTemplate amqpTemplate;
	private String featureToggleName = "Toggle.BackgroundProcesses";
	private MessageHandler emailSubscriptionHandler;

	@Autowired
	private FeatureToggleChecker featureToggleChecker;
	
	@Autowired
	@Qualifier("EmailSubscriptionHandler")
	public void setEmailSubscriptionHandler(MessageHandler emailSubscriptionHandler) {
		this.emailSubscriptionHandler = emailSubscriptionHandler;
	}

	@Autowired
	@Qualifier("EmailSubscriptionTemplate")
	public void setAmqpTemplate(RabbitTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void setFeatureToggleChecker(FeatureToggleChecker featureToggleChecker) {
		this.featureToggleChecker = featureToggleChecker;
	}
	
	public void setFeatureToggleName(String featureToggleName) {
		this.featureToggleName = featureToggleName;
	}

	@Scheduled(fixedDelay=30000)
	public void pollForMessage() {
		if (featureToggleChecker.isFeatureEnabled(featureToggleName)) {
			String inhibitSubscriptionsOnThisServer = System.getProperty("pics.activate_subscription_cron");
			if(inhibitSubscriptionsOnThisServer != null && !inhibitSubscriptionsOnThisServer.isEmpty()) {
				Message message = amqpTemplate.receive();
				if (message != null) {
					emailSubscriptionHandler.handle(message);
				}
			}
		}
	}
}
