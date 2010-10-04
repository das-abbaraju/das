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
	
	private User user;
	private Account account;

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
		tokens.clear();
	}

	protected EmailQueue buildEmail(User user, String serverName) throws Exception {
		EmailQueue email = null;

		if (tokens.size() > 0) {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(templateID);
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			// TODO remove this after we update the templates from username to
			// user.name
			emailBuilder.addToken("username", user.getName());
			emailBuilder.addToken("user", user);

			String seed = "u" + user.getId() + "t" + templateID;
			String confirmLink = serverName + "EmailUserUnsubscribe.action?id=" + user.getId() + "&sub=" + subscription
					+ "&key=" + Strings.hashUrlSafe(seed);
			emailBuilder.addToken("confirmLink", confirmLink);

			emailBuilder.addToken("subscription", subscription);

			emailBuilder.addAllTokens(tokens);
			emailBuilder.setToAddresses(user.getEmail());

			email = emailBuilder.build();
			email.setViewableById(Account.PRIVATE);
			email.setCreatedBy(user);
		}

		return email;
	}

	public void process() throws Exception {
		String serverName = getServerName();
		if (user != null) {
			// For Ad hoc Testing
			// http://localhost:8080/picsWeb2/SubscriptionCron.action?userID=941&accountID=16&timePeriod=Monthly&subs=ContractorRegistration
			if (account == null)
				account = user.getAccount();
			
			setup(account); // Send the account object to the sub-classes
			EmailQueue emailToSend = buildEmail(user, serverName);

			if (emailToSend != null) {
				EmailSender.send(emailToSend);
			}
			return;
		}
		Map<Account, Set<EmailSubscription>> accountMap = getSubscriptionsByAccount();
		for (Map.Entry<Account, Set<EmailSubscription>> entry : accountMap.entrySet()) {
			setup(entry.getKey()); // Send the account object to the sub-classes

			// get the recipients 
			Set<User> recipients = getRecipients(entry.getValue());
			for (User user : recipients) {
				EmailQueue emailToSend = buildEmail(user, serverName);

				if (emailToSend != null) {
					EmailSender.send(emailToSend);
				}
			}

			tearDown(entry.getValue());
		}
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
}