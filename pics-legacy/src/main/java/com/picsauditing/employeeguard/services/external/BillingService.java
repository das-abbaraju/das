package com.picsauditing.employeeguard.services.external;

import com.picsauditing.jpa.entities.Account;

import java.util.Iterator;
import java.util.List;

/**
 * @see com.picsauditing.actions.billing.ProductServiceQuery
 *      <p/>
 *      Stand in for billing logic
 */
public class BillingService {
	public <E extends Account> List<E> filterEmployeeGUARDAccounts(List<E> accounts) {
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

	private <E extends Account> boolean operatorHasEmployeeGUARD(E account) {
		return account.isRequiresCompetencyReview() || account.isRequiresOQ();
	}

	private <E extends Account> boolean contractorHasEmployeeGUARD(E account) {
//		ContractorAccount contractorAccount = (ContractorAccount) account;
//
//		if (contractorAccount.getFees().containsKey(FeeClass.EmployeeGUARD) && !contractorAccount.getFees().get(FeeClass.EmployeeGUARD).getNewAmount().equals(BigDecimal.ZERO.setScale(2))) {
//			return true;
//		}
//
//		return false;
		// FIXME
		return true;
	}
}
