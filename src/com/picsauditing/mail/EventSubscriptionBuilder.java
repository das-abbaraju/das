package com.picsauditing.mail;

import java.util.Date;
import java.util.List;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailSubscription;

public class EventSubscriptionBuilder {

	public static void contractorFinishedEvent(EmailSubscriptionDAO subscriptionDAO, ContractorOperator co)
			throws Exception {
		Date now = new Date();

		List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.ContractorFinished,
				SubscriptionTimePeriod.Event, co.getOperatorAccount().getId());

		for (EmailSubscription subscription : subscriptions) {
			EmailBuilder builder = new EmailBuilder();
			builder.setTemplate(63);
			builder.setFromAddress("info@picsauditing.com");
			builder.addToken("contractor", co.getContractorAccount());
			builder.addToken("operator", co.getOperatorAccount());
			builder.setUser(subscription.getUser());

			EmailSender.send(builder.build());

			subscription.setLastSent(now);
			subscriptionDAO.save(subscription);
		}
	}
}
