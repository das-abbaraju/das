package com.picsauditing.mail.subscription;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.OpenTasks;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;

public class OpenTasksSubscription extends SubscriptionBuilder {
	@Autowired
	private OpenTasks openTasks;

	@Override
	public void process(EmailSubscription subscription) throws IOException {

		if (subscription.getUser().getAccount().isContractor()) {
			try {
				List<String> tasks = openTasks.getOpenTasks((ContractorAccount) subscription.getUser().getAccount(),
						subscription.getUser());
				if (!tasks.isEmpty())
					tokens.put("tasks", tasks);
			} catch (Exception e) {

			}
		}
		EmailQueue emailToSend = buildEmail(subscription);

		if (emailToSend != null) {
			sender.send(emailToSend);
		}

		tokens.clear();

	}

	
}
