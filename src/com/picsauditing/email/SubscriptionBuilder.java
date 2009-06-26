package com.picsauditing.email;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.util.SpringUtils;

public abstract class SubscriptionBuilder {

	protected EmailSubscriptionDAO subscriptionDAO;
	protected Subscription subscription;
	protected List<EmailSubscription> subscriptions;

	public SubscriptionBuilder(Subscription subscription) {
		subscriptionDAO = (EmailSubscriptionDAO) SpringUtils.getBean("EmailSubscriptionDAO");
		this.subscription = subscription;
		subscriptions = subscriptionDAO.findBySubscription(subscription);
	}

	public boolean isSendEmail(EmailSubscription sub) {
		Calendar calendar = Calendar.getInstance();

		boolean send = true;

		switch (sub.getTimePeriod()) {
		case None:
			send = false;
			break;
		case Daily:
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			send = !sub.getLastSent().after(calendar.getTime());
			break;
		case Weekly:
			calendar.add(Calendar.WEEK_OF_YEAR, -1);
			send = !sub.getLastSent().after(calendar.getTime());
			break;
		case Monthly:
			calendar.add(Calendar.MONTH, -1);
			send = !sub.getLastSent().after(calendar.getTime());
			break;
		case Quarterly:
			calendar.add(Calendar.MONTH, -3);
			send = !sub.getLastSent().after(calendar.getTime());
			break;

		default:
			send = true;
			break;
		}

		return send;
	}

	protected List<EmailSubscription> getSubscriptions() {
		if (subscriptions == null)
			subscriptionDAO.findBySubscription(subscription);
		return subscriptions;
	}

	public abstract void process();
}
