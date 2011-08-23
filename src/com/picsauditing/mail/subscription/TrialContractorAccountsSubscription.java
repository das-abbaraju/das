package com.picsauditing.mail.subscription;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;

public class TrialContractorAccountsSubscription extends SubscriptionBuilder {
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;

	@Override
	public void process(EmailSubscription subscription) {
		OperatorAccount o = (OperatorAccount) subscription.getUser().getAccount();

		List<ContractorOperator> trialAccounts = contractorOperatorDAO.findPendingApprovalContractors(o.getId(), true,
				o.isCorporate());
		if (trialAccounts.size() > 0) {
			tokens.put("trialContractors", trialAccounts);
		}
	}
}
