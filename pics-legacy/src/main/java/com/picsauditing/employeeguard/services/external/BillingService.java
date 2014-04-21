package com.picsauditing.employeeguard.services.external;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;

import java.util.Iterator;
import java.util.List;

/**
 * Stand in for billing logic
 */
public class BillingService {

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
		return ((OperatorAccount) account).isRequiresEmployeeGuard();
	}

	/**
	 * Waiting on requirements for how to bill contractors with EmployeeGUARD operators
	 *
	 * @param account
	 * @param <E>
	 * @return
	 */
	private <E extends Account> boolean contractorHasEmployeeGUARD(final E account) {
		return ((ContractorAccount) account).isHasEmployeeGuard();
	}
}
