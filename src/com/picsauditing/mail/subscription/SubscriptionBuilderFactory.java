package com.picsauditing.mail.subscription;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.mail.OpenTasksSubscription;
import com.picsauditing.mail.Subscription;

public class SubscriptionBuilderFactory {
	@Autowired
	OpenTasksSubscription tasks;

	// Subscription => Builder

	public SubscriptionBuilder getBuilder(Subscription subscription) {
		if (subscription == Subscription.OpenTasks)
			return null;

		return null;
	}
}
