package com.picsauditing.mail;

import java.util.Collection;
import java.util.Date;
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
	protected int templateID = 0;
	protected String serverName = null;

	protected Date now = new Date();

	protected Map<String, Object> tokens = new HashMap<String, Object>();

	private List<EmailSubscription> subscriptions;

	protected SelectSQL sql;

	public SubscriptionBuilder(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO) {
		this.subscriptionDAO = subscriptionDAO;
		this.subscription = subscription;
		this.timePeriod = timePeriod;
	}

	protected List<EmailSubscription> getSubscriptions() {
		if (subscriptions == null)
			subscriptions = subscriptionDAO.find(subscription, timePeriod);

		return subscriptions;
	}

	protected Set<User> getRecipients(Collection<EmailSubscription> subs) {
		Set<User> result = new HashSet<User>();

		for (EmailSubscription sub : subs) {
			result.add(sub.getUser());
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

	protected void tearDown(Set<EmailSubscription> subs) {
		for (EmailSubscription sub : subs) {
			sub.setLastSent(now);
			subscriptionDAO.save(sub);
		}
	}

	protected EmailQueue buildEmail(User user, String serverName) throws Exception {
		EmailQueue email = null;

		if (tokens.size() > 0) {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(templateID);
			emailBuilder.setFromAddress("info@picsauditing.com");
			emailBuilder.addToken("username", user.getName());

			String seed = "u" + user.getId() + "t" + templateID;
			String confirmLink = serverName + "EmailUserUnsubscribe.action?id=" + user.getId() + "&sub="
					+ subscription.getDescription() + "&key=" + Strings.hashUrlSafe(seed);
			emailBuilder.addToken("confirmLink", confirmLink);
			
			emailBuilder.addToken("subscription", subscription);

			emailBuilder.addAllTokens(tokens);
			emailBuilder.setToAddresses(user.getEmail());

			email = emailBuilder.build();
		}

		return email;
	}

	public void process() throws Exception {
		Map<Account, Set<EmailSubscription>> accountMap = getSubscriptionsByAccount();
		String serverName = getServerName();
		for (Map.Entry<Account, Set<EmailSubscription>> entry : accountMap.entrySet()) {
			setup(entry.getKey()); // Send the account object to the sub-classes

			// get the recipients
			Set<User> recipients = getRecipients(entry.getValue());
			for (User user : recipients) {
				EmailQueue emailToSend = buildEmail(user, serverName);

				if (emailToSend != null) {
					// TODO Send the email
					if (isRequiresHTML(emailToSend.getEmailTemplate().getId())) {
						emailToSend.setHtml(true);
					}
					EmailSender.send(emailToSend);
				}
			}

			tearDown(entry.getValue());
		}
	}

	public boolean isRequiresHTML(int templateID) {
		if (templateID == 60)
			return true;
		if (templateID == 61)
			return true;
		if (templateID == 62)
			return true;
		if (templateID == 65)
			return true;
		return false;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
}
