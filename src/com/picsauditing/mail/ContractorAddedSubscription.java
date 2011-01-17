package com.picsauditing.mail;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ContractorAddedSubscription extends SubscriptionBuilder {
	public ContractorAddedSubscription(SubscriptionTimePeriod timePeriod,
			EmailSubscriptionDAO subscriptionDAO) {
		super(Subscription.ContractorAdded, timePeriod, subscriptionDAO);
		this.templateID = 107;
	}

	@Override
	protected void setup(Account a) {
		Set<OperatorAccount> children = new HashSet<OperatorAccount>();
		OperatorAccount operator = (OperatorAccount) a;
		
		if (!a.isCorporate()) // adding self if not corporate
			children.add(operator);
		else 
			children.addAll(operator.getOperatorChildren());
		

		Map<OperatorAccount, Map<ContractorAccount, ContractorOperator>> operators = new TreeMap<OperatorAccount, Map<ContractorAccount, ContractorOperator>>(getOperatorComparator());

		// Looking through children to find contractors that were added in
		// time period.
		for (OperatorAccount child : children) {
			for(ContractorOperator co : child.getContractorOperators()){
				// If added after necessary time period
				if(co.getContractorAccount().getStatus().isActive()
						&& co.getCreationDate().after(timePeriod.getComparisonDate())){
					if(operators.get(child) == null)
						operators.put(child, new TreeMap<ContractorAccount,ContractorOperator>(getContractorComparator()));
					operators.get(child).put(co.getContractorAccount(), co);
				}
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
	
	public Comparator<ContractorAccount> getContractorComparator() {
		return new Comparator<ContractorAccount>() {

			@Override
			public int compare(ContractorAccount c1, ContractorAccount c2) {
				return c1.getName().compareToIgnoreCase(c2.getName());
			}
		};
	}
}