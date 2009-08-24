package com.picsauditing.mail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorOperatorFlag;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

public class FlagColorSubscription extends SubscriptionBuilder {

	private ContractorOperatorFlagDAO flagDAO;
	private FlagColor flagColor;

	public FlagColorSubscription(Subscription subscription, SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO, ContractorOperatorFlagDAO flagDAO) {
		super(subscription, timePeriod, subscriptionDAO);
		this.flagDAO = flagDAO;
		this.templateID = 65;
		if (subscription.equals(Subscription.RedFlags))
			flagColor = FlagColor.Red;
		else if (subscription.equals(Subscription.AmberFlags))
			flagColor = FlagColor.Amber;
		else if (subscription.equals(Subscription.GreenFlags))
			flagColor = FlagColor.Green;
	}

	@Override
	protected void setup(Account a) {
		Set<Integer> conIds = new HashSet<Integer>();
		OperatorAccount o = (OperatorAccount) a;

		for (ContractorOperator co : o.getContractorOperators()) {
			conIds.add(co.getContractorAccount().getId());
		}

		List<ContractorOperatorFlag> flags = flagDAO.findWhere("operatorAccount.id = " + a.getId()
				+ " AND contractorAccount.id IN (" + Strings.implode(conIds, ",") + ") AND flagColor = '" + flagColor
				+ "'");
		if (flags.size() > 0) {
			tokens.put("flags", flags);
			tokens.put("flagColor", flagColor);
		}
	}
}
