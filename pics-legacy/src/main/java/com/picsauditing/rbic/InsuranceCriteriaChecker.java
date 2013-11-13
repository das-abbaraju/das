package com.picsauditing.rbic;

import com.picsauditing.jpa.entities.*;

public class InsuranceCriteriaChecker {

    public static boolean meetsCriteria(AuditData contractorsLimit, InsuranceCriteriaContractorOperator insuranceCriteria) throws InvalidAuditDataAnswer {
        AuditData contractorsExcessLimit = null;
        FlagCriteria flagCriteria = insuranceCriteria.getFlagCriteria();
        if (flagCriteria.getOptionCode() == FlagCriteriaOptionCode.ExcessAggregate ||
                flagCriteria.getOptionCode() == FlagCriteriaOptionCode.ExcessEachOccurrence) {
            contractorsExcessLimit = findContractorsExcessLimits(flagCriteria, insuranceCriteria.getContractorAccount());
        }
        int first;
        int second;
        try {
            first = Integer.parseInt(contractorsLimit.getAnswer().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new InvalidAuditDataAnswer("Unable to parse audit data id: " + contractorsLimit.getId() +
                    " with answer: " + contractorsLimit.getAnswer(), e);
        }
        try {
            second = contractorsExcessLimit != null ? Integer.parseInt(contractorsExcessLimit.getAnswer().replace(",", "")) : 0;
        } catch (NumberFormatException e) {
            throw new InvalidAuditDataAnswer("Unable to parse audit data id: " + contractorsExcessLimit.getId() +
                    " with answer: " + contractorsExcessLimit.getAnswer(), e);
        }
        return first + second >= insuranceCriteria.getInsuranceLimit();
    }

    private static AuditData findContractorsExcessLimits(FlagCriteria flagCriteria, ContractorAccount contractor) {
        for (ContractorAudit ca : contractor.getAudits()) {
            if (ca.getAuditType().getId() == AuditType.EXCESS_LIABILITY) {
                for (AuditData auditData : ca.getData()) {
                    if (flagCriteria.includeExcess() == auditData.getQuestion().getId()) {
                        return auditData;
                    }
                }
                break;
            }
        }

        return null;
    }
}