package com.picsauditing.mail;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.util.Strings;

public class EventSubscriptionBuilder {

	public static void contractorFinishedEvent(EmailSubscriptionDAO subscriptionDAO, ContractorOperator co)
			throws Exception {
		Date now = new Date();

		List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.ContractorFinished,
				SubscriptionTimePeriod.Event, co.getOperatorAccount().getId());

		Set<String> emails = new HashSet<String>();
		for (EmailSubscription subscription : subscriptions) {
			emails.add(subscription.getUser().getEmail());
			subscription.setLastSent(now);
			subscriptionDAO.save(subscription);
		}
		String recipients = Strings.implode(emails, ",");

		EmailBuilder builder = new EmailBuilder();
		builder.setTemplate(63);
		builder.setToAddresses(recipients);
		builder.setFromAddress("info@picsauditing.com");
		builder.addToken("contractor", co.getContractorAccount());
		builder.addToken("operator", co.getOperatorAccount());

		EmailSender.send(builder.build());
	}
}
