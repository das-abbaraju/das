package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorAddedSubscription extends SubscriptionBuilder {

	private ContractorAccountDAO conDAO;

	public ContractorAddedSubscription(SubscriptionTimePeriod timePeriod, EmailSubscriptionDAO subscriptionDAO,
			ContractorAccountDAO conDAO) {
		super(Subscription.ContractorAdded, timePeriod, subscriptionDAO);
		this.conDAO = conDAO;
		this.templateID = 107;
	}

	@Override
	protected void setup(Account a) {
		List<ContractorAccount> contractors = conDAO.findRecentlyAddedContractorsByOperator(a.getId(),
				timePeriod.getComparisonDate(), now);

		if (contractors.size() > 0)
			tokens.put("contractors", contractors);
	}
}
