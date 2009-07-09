package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorRegistrationSubscription extends SubscriptionBuilder {

	private ContractorAccountDAO conDAO;

	public ContractorRegistrationSubscription(SubscriptionTimePeriod timePeriod, EmailSubscriptionDAO subscriptionDAO,
			ContractorAccountDAO conDAO) {
		super(Subscription.ContractorRegistration, timePeriod, subscriptionDAO);
		this.conDAO = conDAO;
		this.templateID = 62;
	}

	@Override
	protected void setup(Account a) {
		List<ContractorAccount> contractors = conDAO.findNewContractorsByOperator(a.getId(),
				timePeriod.getComparisonDate(), now);

		if (contractors.size() > 0)
			tokens.put("contractors", contractors);
	}
}
