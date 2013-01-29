package com.picsauditing.mail.subscription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.OpenTasks;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenTasksSubscription extends SubscriptionBuilder {
	final static Logger logger = LoggerFactory.getLogger(OpenTasksSubscription.class); 
	
	@Autowired
	private OpenTasks openTasks;

	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		User user = subscription.getUser();
		try {
			assert user.isActiveB();
			assert user.getAccount().isContractor();
			assert user.getAccount().getStatus().isActive();
			assert subscription.getSubscription() == Subscription.OpenTasks;
			List<String> tasks = openTasks.getOpenTasksEmail((ContractorAccount) user.getAccount(),user);
			if (!tasks.isEmpty())
				tokens.put("tasks", tasks);
		} catch (Exception e) {
			logger.error("Error attempting to gather the open tasks for an Open Tasks Subscription for user "+user,e);
		}

		return tokens;
	}
}
