package com.picsauditing.access.permissions.service;

import com.picsauditing.access.permissions.entities.*;

import java.util.*;

public class AccountService {
    public static List<OperatorAccount> getLinkedGeneralContractorOperatorAccounts(OperatorAccount operatorAccount) {
        List<OperatorAccount> linkedGeneralContractorOperatorAccounts = new ArrayList<>();
        for (Facility facility : operatorAccount.getLinkedGeneralContractors()) {
            OperatorAccount linkedGeneralContractor = facility.getOperator();

            if (linkedGeneralContractor.getStatus() == AccountStatus.Active
                    || (operatorAccount.getStatus() == AccountStatus.Demo && linkedGeneralContractor.getStatus() == AccountStatus.Demo)) {
                linkedGeneralContractorOperatorAccounts.add(linkedGeneralContractor);
            }
        }

        return linkedGeneralContractorOperatorAccounts;
    }

    public static List<OperatorAccount> getLinkedClientSites(OperatorAccount operatorAccount) {
        List<OperatorAccount> linkedClientSites = new ArrayList<>();
        for (Facility facility : operatorAccount.getLinkedClients()) {
            OperatorAccount linkedClientSite = facility.getCorporate();

            if (linkedClientSite.getStatus() == AccountStatus.Active
                    || (operatorAccount.getStatus() == AccountStatus.Demo && linkedClientSite.getStatus() == AccountStatus.Demo)) {
                linkedClientSites.add(linkedClientSite);
            }
        }

        return linkedClientSites;
    }

    public static boolean isGeneralContractorFree(OperatorAccount operatorAccount) {
        return operatorAccount.isGeneralContractor() && "No".equals(operatorAccount.getDoContractorsPay());
    }

	public static boolean isContractor(Account account) {
		return Account.CONTRACTOR_ACCOUNT_TYPE.equals(account.getType());
	}

	public static boolean isOperator(Account account) {
		return Account.OPERATOR_ACCOUNT_TYPE.equals(account.getType());
	}

	public static boolean isCorporate(Account account) {
		return Account.CORPORATE_ACCOUNT_TYPE.equals(account.getType());
	}

    /**
     * Please use sparingly!! This does a call to a Spring loaded DAO
     *
     * @return Set of AuditTypeIDs
     */
    public static Set<Integer> getVisibleAuditTypes(OperatorAccount operatorAccount) {
        // TODO: We need to find a way to get this without running a DAO call
//        AuditDecisionTableDAO dao = (AuditDecisionTableDAO) SpringUtils.getBean("AuditDecisionTableDAO");
//        return dao.getAuditTypes(operatorAccount);
        return new HashSet<>();
    }

    public static boolean isCanContractorAddClientSites(ContractorAccount contractorAccount) {
        for (OperatorAccount operator : getOperatorAndCorporateAccounts(contractorAccount)) {
            if (operator.getId() == 42399) { // IHG
                return false;
            }
        }

        return true;
    }

    public static List<OperatorAccount> getOperatorAndCorporateAccounts(ContractorAccount contractorAccount) {
        List<OperatorAccount> list = new ArrayList<>();
        for (ContractorOperator co : contractorAccount.getOperators()) {
            if (co.getOperatorAccount().getStatus() != AccountStatus.Deactivated
                    && co.getOperatorAccount().getStatus() != AccountStatus.Deleted) {
                list.add(co.getOperatorAccount());
            }
        }
        Collections.sort(list);
        return list;
    }

}
