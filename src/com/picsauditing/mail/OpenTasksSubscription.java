package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.PICS.OpenTasks;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;

public class OpenTasksSubscription extends SubscriptionBuilder {
	private OpenTasks openTasks;

	public OpenTasksSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO, OpenTasks openTasks) {
		super(subscription, timePeriod, subscriptionDAO);
		this.openTasks = openTasks;
		this.templateID = 168;
	}

	@Override
	protected void setup(Account a) {
	};

	@Override
	public void process() throws Exception {
		String serverName = getServerName();

		List<EmailSubscription> subscriptions = getSubscriptions();
		for (EmailSubscription subscription : subscriptions) {
			if (subscription.getUser().getAccount().isContractor()) {
				try {
					List<String> tasks = openTasks.getOpenTasks(
							(ContractorAccount) subscription.getUser().getAccount(), subscription.getUser());
					if (!tasks.isEmpty())
						tokens.put("tasks", tasks);
				} catch (Exception e) {

				}
			}
			EmailQueue emailToSend = buildEmail(subscription.getUser(), serverName);

			if (emailToSend != null) {
				EmailSender.send(emailToSend);
			}

			tokens.clear();
		}

		tearDown(subscriptions);
	}
}
