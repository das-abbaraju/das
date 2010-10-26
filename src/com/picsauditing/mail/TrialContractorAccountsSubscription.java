package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class TrialContractorAccountsSubscription extends SubscriptionBuilder {

	private ContractorOperatorDAO contractorOperatorDAO;

	public TrialContractorAccountsSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO, ContractorOperatorDAO contractorOperatorDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.templateID = 71;
	}

	@Override
	protected void setup(Account a) {
		OperatorAccount o = (OperatorAccount) a;

		List<ContractorOperator> trialAccounts = contractorOperatorDAO.findPendingApprovalContractors(o.getId(), true, o.isCorporate()); 
		if (trialAccounts.size() > 0) {
			tokens.put("trialContractors", trialAccounts);
		}
	}
}
