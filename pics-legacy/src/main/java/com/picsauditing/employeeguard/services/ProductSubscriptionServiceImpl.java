package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.AccountEmployeeGuardDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.OperatorAccount;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductSubscriptionServiceImpl implements ProductSubscriptionService {

	@Autowired
	private AccountEmployeeGuardDAO accountEmployeeGuardDAO;

	@Override
	public boolean hasEmployeeGUARD(Account account) {
		if (account.isContractor()) {
			return contractorHasEmployeeGUARD((ContractorAccount) account);
		}

		if (account.isOperatorCorporate()) {
			return operatorHasEmployeeGUARD((OperatorAccount) account);
		}

		return false;
	}

	private boolean contractorHasEmployeeGUARD(ContractorAccount contractor) {
		return contractorHasEmployeeGUARDOperator(contractor);
	}

	public boolean operatorHasEmployeeGUARD(OperatorAccount operator) {
		return accountEmployeeGuardDAO.isEmployeeGUARDEnabled(operator.getId());
	}

	private boolean contractorHasEmployeeGUARDOperator(ContractorAccount contractor) {
		for (OperatorAccount operator : contractor.getOperatorAccounts()) {
			if (operatorHasEmployeeGUARD(operator)) {
				return true;
			}
		}

		return false;
	}
}
