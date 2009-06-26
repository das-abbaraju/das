package com.picsauditing.email;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.SpringUtils;

public abstract class SubscriptionBuilder {

	protected EmailSubscriptionDAO subscriptionDAO;
	protected Subscription subscription;
	protected List<EmailSubscription> subscriptions;

	protected SelectSQL sql;

	public SubscriptionBuilder(Subscription subscription) {
		subscriptionDAO = (EmailSubscriptionDAO) SpringUtils.getBean("EmailSubscriptionDAO");
		this.subscription = subscription;
	}

	public boolean isSendEmail(EmailSubscription sub) {
		if (sub.getTimePeriod().equals(SubscriptionTimePeriod.None))
			return false;
		return !sub.getLastSent().after(sub.getTimePeriod().getCompaisonDate());
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

	protected Map<Account, Set<EmailSubscription>> getSubscriptionsByLastSentAndAccount() {
		Map<Account, Set<EmailSubscription>> result = new HashMap<Account, Set<EmailSubscription>>();

		for (EmailSubscription sub : getSubscriptions()) {
			if (result.get(sub.getUser().getAccount()) == null)
				result.put(sub.getUser().getAccount(), new HashSet<EmailSubscription>());

			result.get(sub.getUser().getAccount()).add(sub);
		}

		return result;
	}

	protected abstract List<BasicDynaBean> runSql();

	protected abstract EmailQueue buildEmail();

	public abstract void process();
}
