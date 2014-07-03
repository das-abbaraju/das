package com.picsauditing.auditbuilder.service;

import com.picsauditing.auditbuilder.entities.*;
import com.picsauditing.auditbuilder.util.Grepper;

import java.util.*;

public class AccountService {
    public static final String OPERATOR_ACCOUNT_TYPE = "Operator";
    public static final String CORPORATE_ACCOUNT_TYPE = "Corporate";
    public static final String CONTRACTOR_ACCOUNT_TYPE = "Contractor";

    public static Set<ContractorType> getAccountTypes(Account account) {
        Set<ContractorType> types = new HashSet<>();
        if (account.isMaterialSupplier()) {
            types.add(ContractorType.Supplier);
        }
        if (account.isOnsiteServices()) {
            types.add(ContractorType.Onsite);
        }
        if (account.isOffsiteServices()) {
            types.add(ContractorType.Offsite);
        }
        if (account.isTransportationServices()) {
            types.add(ContractorType.Transportation);
        }
        return types;
    }

	public static List<ContractorOperator> getNonCorporateOperators(ContractorAccount contractorAccount) {
		return new Grepper<ContractorOperator>() {

			@Override
			public boolean check(ContractorOperator t) {
				return !isCorporate(t.getOperatorAccount()) && !t.getOperatorAccount().getStatus().isDeactivated()
						&& !t.getOperatorAccount().getStatus().isDeleted();
			}
		}.grep(contractorAccount.getOperators());
	}

    public static List<OperatorAccount> getOperatorAccounts(ContractorAccount contractorAccount) {
        List<OperatorAccount> list = new ArrayList<>();
        for (ContractorOperator co : contractorAccount.getOperators()) {
            if (isOperator(co.getOperatorAccount()) && !co.getOperatorAccount().getStatus().isDeactivated()
                    && !co.getOperatorAccount().getStatus().isDeleted()) {
                list.add(co.getOperatorAccount());
            }
        }
        Collections.sort(list);
        return list;
    }

    public static boolean isCorporate(Account account) {
        return CORPORATE_ACCOUNT_TYPE.equals(account.getType());
    }

    public static boolean isOperator(Account account) {
        return OPERATOR_ACCOUNT_TYPE.equals(account.getType());
    }

    public static boolean isContractor(Account account) {
        return CONTRACTOR_ACCOUNT_TYPE.equals(account.getType());
    }

	public static int getRulePriorityLevel(OperatorAccount operatorAccount) {
		switch (operatorAccount.getId()) {
			case 4:
				return 1;
			case 5:
			case 6:
			case 7:
			case 8:
				return 2;
		}

		if (operatorAccount.isPrimaryCorporate()) {
			return 3;
		}

		if (isCorporate(operatorAccount)) {
			return 4;
		}

		return 5;
	}

}
