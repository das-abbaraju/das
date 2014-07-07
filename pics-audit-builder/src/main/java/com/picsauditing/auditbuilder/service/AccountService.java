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

	public static List<Integer> getOperatorHeirarchy(OperatorAccount operatorAccount) {
        return getOperatorHeirarchy(operatorAccount, true);
    }

	public static List<Integer> getOperatorHeirarchy(OperatorAccount operatorAccount, boolean includePicsConsortium) {
		List<Integer> list = new ArrayList<>();
		// Add myself
		list.add(operatorAccount.getId());

		OperatorAccount topAccount = getTopAccount(operatorAccount);
		for (Facility facility : operatorAccount.getCorporateFacilities()) {
			if (!facility.getCorporate().equals(topAccount)) {
				// Add parent's that aren't my primary parent
                if (includePicsConsortium || !facility.getCorporate().isInPicsConsortium()) {
				    list.add(facility.getCorporate().getId());
                }
			}
		}
		if (!topAccount.equals(operatorAccount)) {
			// Add my parent
			list.add(topAccount.getId());
		}
		return list;
	}

	public static OperatorAccount getTopAccount(OperatorAccount operatorAccount) {
		OperatorAccount topAccount = operatorAccount;
		if (operatorAccount.getParent() != null) {
			topAccount = operatorAccount.getParent();
		}

		for (Facility facility : operatorAccount.getCorporateFacilities()) {
			if (facility.getCorporate().isPrimaryCorporate()) {
				topAccount = facility.getCorporate();
				break;
			}
		}
		return topAccount;
	}

}
