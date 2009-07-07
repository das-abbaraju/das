package com.picsauditing.mail;

import java.util.Date;
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
	}

	@Override
	protected void setup(Account a) {
		templateID = 60;
		
		List<ContractorOperatorFlag> flags = flagDAO.findFlagChangedByOperatorAndRange(a.getId(), timePeriod
				.getComparisonDate(), new Date());
		
		tokens.put("flags", flags);
	}
}
