package com.picsauditing.mail;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

public abstract class SubscriptionBuilder {

	protected EmailSubscriptionDAO subscriptionDAO;
	protected Subscription subscription;
	protected SubscriptionTimePeriod timePeriod;
	protected int templateID;

	protected Map<String, Object> tokens = new HashMap<String, Object>();

	private List<EmailSubscription> subscriptions;

	protected SelectSQL sql;

	public SubscriptionBuilder(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO) {
		this.subscriptionDAO = subscriptionDAO;
		this.subscription = subscription;
		this.timePeriod = timePeriod;
	}

	public boolean isSendEmail(EmailSubscription sub) {
		if (sub.getTimePeriod().equals(SubscriptionTimePeriod.None))
			return false;

		return !sub.getLastSent().after(sub.getTimePeriod().getComparisonDate());
	}

	protected List<EmailSubscription> getSubscriptions() {
		if (subscriptions == null)
			subscriptions = subscriptionDAO.find(subscription, timePeriod);

		return subscriptions;
	}

	protected Set<String> getRecipients(Collection<EmailSubscription> subs) {
		Set<String> result = new HashSet<String>();

		for (EmailSubscription sub : subs) {
			result.add(sub.getUser().getEmail());
		}

		return result;
	}

	protected Map<Account, Set<EmailSubscription>> getSubscriptionsByAccount() {
		Map<Account, Set<EmailSubscription>> result = new HashMap<Account, Set<EmailSubscription>>();

		for (EmailSubscription sub : getSubscriptions()) {
			if (result.get(sub.getUser().getAccount()) == null)
				result.put(sub.getUser().getAccount(), new HashSet<EmailSubscription>());

			result.get(sub.getUser().getAccount()).add(sub);
		}

		return result;
	}

	protected abstract void setup(Account a);

	protected EmailQueue buildEmail(String recipients) throws Exception {
		EmailQueue email = null;

		if (tokens.size() > 0) {

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(templateID);
			emailBuilder.addAllTokens(tokens);
			emailBuilder.setCcAddresses(recipients);
			emailBuilder.setUser(new User(2357));

			email = emailBuilder.build();
		}

		return email;
	}

	public void process() throws Exception {
		// TODO Auto-generated method stub
		Map<Account, Set<EmailSubscription>> accountMap = getSubscriptionsByAccount();

		for (Map.Entry<Account, Set<EmailSubscription>> entry : accountMap.entrySet()) {
			setup(entry.getKey()); // Send the account object to the sub-classes

			// get the recipients
			Set<String> recipients = getRecipients(entry.getValue());
			EmailQueue emailToSend = buildEmail(Strings.implode(recipients, ","));

			if (emailToSend != null) {
				// Send the email
				// EmailSender.send(emailToSend);
			}
		}
	}
}
