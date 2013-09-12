package com.picsauditing.mail.subscription;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ContractorAddedSubscription extends SubscriptionBuilder {

	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		Set<OperatorAccount> children = new HashSet<OperatorAccount>();
		OperatorAccount operator = (OperatorAccount) subscription.getUser().getAccount();

		if (!operator.isCorporate()) // adding self if not corporate
			children.add(operator);
		else
			children.addAll(operator.getOperatorChildren());

		Map<OperatorAccount, Map<ContractorAccount, ContractorOperator>> operators = new TreeMap<OperatorAccount, Map<ContractorAccount, ContractorOperator>>(
				getOperatorComparator());

		// Looking through children to find contractors that were added in
		// time period.
		for (OperatorAccount child : children) {
			for (ContractorOperator co : child.getContractorOperators()) {
				// If added after necessary time period
				if (co.getContractorAccount().getStatus().isActive()
						&& co.getCreationDate().after(subscription.getTimePeriod().getComparisonDate())) {
					if (operators.get(child) == null)
						operators.put(child, new TreeMap<ContractorAccount, ContractorOperator>(
								getContractorComparator()));
					operators.get(child).put(co.getContractorAccount(), co);
				}
			}
		}

		if (operators.size() > 0)
			tokens.put("operators", operators);

		return tokens;
	}

	private Comparator<OperatorAccount> getOperatorComparator() {
		return new Comparator<OperatorAccount>() {
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};
	}

	private Comparator<ContractorAccount> getContractorComparator() {
		return new Comparator<ContractorAccount>() {
			public int compare(ContractorAccount c1, ContractorAccount c2) {
				return c1.getName().compareToIgnoreCase(c2.getName());
			}
		};
	}
}