package com.picsauditing.mail.subscription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.SubBuildListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.OpenTasks;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;

public class OpenTasksSubscription extends SubscriptionBuilder {
	@Autowired
	private OpenTasks openTasks;

	@Override
	public void process(EmailSubscription subscription) {

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
