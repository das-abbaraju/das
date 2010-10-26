package com.picsauditing.mail;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.UserSwitch;

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
		Set<OperatorAccount> children = new HashSet<OperatorAccount>();
		OperatorAccount operator = (OperatorAccount) a;
		
		if (!a.isCorporate()) // adding self if not corporate
			children.add(operator);
		else { // adding children and switch-tos if corporate
			children.addAll(operator.getOperatorChildren());
			for (UserSwitch user : getUser().getSwitchTos())
				if (user.getUser().getAccount().isOperator())
					children.add((OperatorAccount) user.getUser().getAccount());
		}

		Map<OperatorAccount, List<ContractorAccount>> operators = new TreeMap<OperatorAccount, List<ContractorAccount>>(getOperatorComparator());

		// Looking through children to find contractors that were added in
		// time period.
		for (OperatorAccount child : children) {
			List<ContractorAccount> contractors = conDAO.findNewContractorsByOperator(child.getId(),
					timePeriod.getComparisonDate(), now);
			
			if(!contractors.isEmpty()){
				operators.put(child,contractors);
			}
		}

		if (operators.size() > 0)
			tokens.put("operators", operators);
	}
	
	public Comparator<OperatorAccount> getOperatorComparator() {
		return new Comparator<OperatorAccount>() {

			@Override
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};
	}
}
