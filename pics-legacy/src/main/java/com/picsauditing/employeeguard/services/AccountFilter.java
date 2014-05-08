package com.picsauditing.employeeguard.services;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Iterator;
import java.util.List;

class AccountFilter {

	@Autowired
	private ProductSubscriptionService productSubscriptionService;

	public <E extends Account> List<E> filterEmployeeGUARDAccounts(final List<E> accounts) {
		Iterator<E> iterator = accounts.iterator();

		while (iterator.hasNext()) {
			E account = iterator.next();

			if (account.isOperator() && !operatorHasEmployeeGUARD(account)) {
				iterator.remove();
			}

			if (account.isContractor() && !contractorHasEmployeeGUARD(account)) {
				iterator.remove();
			}
		}

		return accounts;
	}

	private <E extends Account> boolean operatorHasEmployeeGUARD(final E account) {
		return productSubscriptionService.hasEmployeeGUARD(account.getId());
	}

	/**
	 * Waiting on requirements for how to bill contractors with EmployeeGUARD operators
	 *
	 * @param account
	 * @param <E>
	 * @return
	 */
	private <E extends Account> boolean contractorHasEmployeeGUARD(final E account) {
		return productSubscriptionService.hasEmployeeGUARD(account.getId());
	}
}
