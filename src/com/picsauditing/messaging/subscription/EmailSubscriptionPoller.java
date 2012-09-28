package com.picsauditing.messaging.subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;

import com.picsauditing.messaging.MessageHandler;
import com.picsauditing.toggle.FeatureToggle;

public class EmailSubscriptionPoller {
	private final Logger logger = LoggerFactory.getLogger(EmailSubscriptionPoller.class);
	
	private RabbitTemplate amqpTemplate;
	private String featureToggleName = FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL;
	private MessageHandler emailSubscriptionHandler;

	@Autowired
	private FeatureToggle featureToggleChecker;
	
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

	public void setFeatureToggleChecker(FeatureToggle featureToggleChecker) {
		this.featureToggleChecker = featureToggleChecker;
	}
	
	public void setFeatureToggleName(String featureToggleName) {
		this.featureToggleName = featureToggleName;
	}

	@Scheduled(fixedDelay = 600000)
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
