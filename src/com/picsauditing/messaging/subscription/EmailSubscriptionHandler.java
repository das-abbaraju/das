package com.picsauditing.messaging.subscription;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.messaging.MessageHandler;

public class EmailSubscriptionHandler implements MessageHandler {
	private Logger logger = LoggerFactory.getLogger(EmailSubscriptionHandler.class);

	@Autowired
	private AppPropertyDAO appPropertyProvider;
	@Autowired
	private EmailSubscriptionDAO emailSubscriptionProvider;
	@Autowired
	private SubscriptionBuilderFactory subscriptionFactory;
	
	@Override
	@Transactional
	public void handle(Message message) {
		if (message == null){
			return;
		}
		if (Boolean.parseBoolean(appPropertyProvider.getProperty("subscription.enable"))) {
			Integer subscriptionId = Integer.parseInt(new String(message.getBody()));
			EmailSubscription emailSubscription = emailSubscriptionProvider.find(subscriptionId);

			if (emailSubscription == null) {
				logger.error("Unable to find email subscription with id {} ", subscriptionId);
				return;
			}

			try {
				SubscriptionBuilder builder = subscriptionFactory.getBuilder(emailSubscription.getSubscription());
				builder.sendSubscription(emailSubscription);
			} catch (Exception e) {
				logger.error("Could not process subscription {}; {}", subscriptionId, e.getStackTrace());
				setSubscriptionToBeReprocessedTomorrow(emailSubscription);
			}

		}
	}

	@Override
	public void handle(List<Message> messages) {
		for (Message message : messages) {
			handle(message);
		}
	}

	private void setSubscriptionToBeReprocessedTomorrow(EmailSubscription emailSubscription) {
		logger.error("Setting sent date to tomorrow for reprocessing");
		try {
			Date lastSent = emailSubscription.getLastSent();
			if (lastSent == null) {
				lastSent = new Date();
			}
			emailSubscription.setLastSent(DateBean.addDays(lastSent, 1));
			emailSubscriptionProvider.save(emailSubscription);
		} catch (Exception notMuchWeCanDoButLogIt) {
			logger.error("Error changing processing to tomorrow subscription {}", emailSubscription.getId());
			logger.error(notMuchWeCanDoButLogIt.getMessage());
		}
	}

}
