package com.picsauditing.mail.subscription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.OpenTasks;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailSubscription;

public class OpenTasksSubscription extends SubscriptionBuilder {
	@Autowired
	private OpenTasks openTasks;

	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		try {
			List<String> tasks = openTasks.getOpenTasks((ContractorAccount) subscription.getUser().getAccount(),
					subscription.getUser());
			if (!tasks.isEmpty())
				tokens.put("tasks", tasks);
		} catch (Exception e) {

		}

		return tokens;
	}
}
