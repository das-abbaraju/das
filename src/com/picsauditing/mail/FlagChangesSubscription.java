package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;

public class FlagChangesSubscription extends SubscriptionBuilder {

	private ContractorOperatorFlagDAO flagDAO;

	public FlagChangesSubscription(SubscriptionTimePeriod timePeriod, EmailSubscriptionDAO subscriptionDAO,
			ContractorOperatorFlagDAO flagDAO) {
		super(Subscription.FlagChanges, timePeriod, subscriptionDAO);
		this.flagDAO = flagDAO;
		this.templateID = 60;
	}

	@Override
	protected void setup(Account a) {
		List<ContractorOperatorFlag> flags = flagDAO.findFlagChangedByOperatorAndRange(a.getId(), timePeriod
				.getComparisonDate(), now);

		if (flags.size() > 0)
			tokens.put("flags", flags);
	}
}
