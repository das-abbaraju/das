package com.picsauditing.email;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.util.SpringUtils;

public abstract class SubscriptionBuilder {

	protected EmailSubscriptionDAO subscriptionDAO;
	protected Subscription subscription;
	protected List<EmailSubscription> subscriptions;

	public SubscriptionBuilder(Subscription subscription) {
		subscriptionDAO = (EmailSubscriptionDAO) SpringUtils.getBean("EmailSubscriptionDAO");
		this.subscription = subscription;
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

	protected Set<String> getRecipients() {
		Set<String> result = new HashSet<String>();

		for (EmailSubscription sub : getSubscriptions()) {
			result.add(sub.getUser().getEmail());
		}

		return result;
	}

	protected Map<Account, Set<String>> getRecipientsMap() {
		Map<Account, Set<String>> result = new HashMap<Account, Set<String>>();

		for (EmailSubscription sub : getSubscriptions()) {
			if (result.get(sub.getUser().getAccount()) == null)
				result.put(sub.getUser().getAccount(), new HashSet<String>());
			
			result.get(sub.getUser().getAccount()).add(sub.getUser().getEmail());
		}

		return result;
	}
	
	protected abstract EmailQueue buildEmail(Map<String, ? extends Object> parameters);	

	public abstract void process();
}
