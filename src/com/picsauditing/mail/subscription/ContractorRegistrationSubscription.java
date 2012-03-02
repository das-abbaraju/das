package com.picsauditing.mail.subscription;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;

public class ContractorRegistrationSubscription extends SubscriptionBuilder {
	@Autowired
	private ContractorAccountDAO conDAO;

	private Comparator<OperatorAccount> getOperatorComparator() {
		return new Comparator<OperatorAccount>() {
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		};
	}

	@Override
	public Map<String, Object> process(EmailSubscription subscription) {
		Map<String, Object> tokens = new HashMap<String, Object>();

		Set<OperatorAccount> children = new HashSet<OperatorAccount>();
		OperatorAccount operator = (OperatorAccount) subscription.getUser().getAccount();

		if (!operator.isCorporate()) // adding self if not corporate
			children.add(operator);
		else { // adding children and switch-tos if corporate
			children.addAll(operator.getOperatorChildren());
			// for (UserSwitch user : getUser().getSwitchTos())
			// if (user.getUser().getAccount().isOperator())
			// children.add((OperatorAccount) user.getUser().getAccount());
		}

		Map<OperatorAccount, List<ContractorAccount>> operators = new TreeMap<OperatorAccount, List<ContractorAccount>>(
				getOperatorComparator());

		// Looking through children to find contractors that were added in
		// time period.
		for (OperatorAccount child : children) {
			List<ContractorAccount> contractors = conDAO.findNewContractorsByOperator(child.getId(), subscription
					.getTimePeriod().getComparisonDate(), new Date());

			if (!contractors.isEmpty()) {
				operators.put(child, contractors);
			}
		}

		if (operators.size() > 0)
			tokens.put("operators", operators);

		return tokens;
	}
}
