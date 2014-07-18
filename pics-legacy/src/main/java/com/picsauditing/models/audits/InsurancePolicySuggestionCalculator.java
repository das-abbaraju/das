package com.picsauditing.models.audits;

import com.picsauditing.jpa.entities.*;

import java.util.HashSet;
import java.util.Set;

public class InsurancePolicySuggestionCalculator {
    public static void calculateSuggestionForAllPolicies(ContractorAccount contractor){
        for (ContractorAudit audit : contractor.getAudits()) {
            if (nonExpiredInsurancePolicy(audit)) {
                calculateSuggestionForPolicy(contractor, audit);
            }
        }
    }

    public static void calculateSuggestionForPolicy(ContractorAccount contractor, ContractorAudit audit) {
        for (ContractorAuditOperator cao : audit.getOperators())
            if (cao.getStatus().after(AuditStatus.Incomplete)) {
                Set<FlagData> flagDataSet = gatherFlagDataFromAppropriateOperators(contractor, cao);

                FlagColor flagColor = calculateCaoStatus(audit.getAuditType(), flagDataSet);
                cao.setFlag(flagColor);
        }
    }

    private static FlagColor calculateCaoStatus(AuditType auditType, Set<com.picsauditing.jpa.entities.FlagData> flagDatas) {
        FlagColor flag = null;
        for (com.picsauditing.jpa.entities.FlagData flagData : flagDatas) {
            if (isInsuranceCriteria(flagData, auditType)) {
                flag = FlagColor.getWorseColor(flag, flagData.getFlag());
                if (flag.isRed()) {
                    return flag;
                }
            }
        }

        if (flag == null) {
            flag = FlagColor.Green;
        }

        return flag;
    }

    private static boolean isInsuranceCriteria(com.picsauditing.jpa.entities.FlagData flagData, AuditType auditType) {
        boolean isAppropriateAudit = true;
        if (flagData.getCriteria().getQuestion() != null && flagData.getCriteria().getQuestion().getAuditType() != null) {
            isAppropriateAudit = flagData.getCriteria().getQuestion().getAuditType().equals(auditType);
        }
        return flagData.getCriteria().isInsurance() && isAppropriateAudit;
    }

    private static Set<FlagData> gatherFlagDataFromAppropriateOperators(ContractorAccount contractor, ContractorAuditOperator cao) {
        Set<FlagData> flagDataSet = new HashSet<>();
        for (ContractorOperator co : contractor.getNonCorporateOperators()) {
            if (cao.hasCaop(co.getOperatorAccount().getId())) {
                flagDataSet.addAll(co.getFlagDatas());
            }

        }
       return flagDataSet;
    }

    private static boolean nonExpiredInsurancePolicy(ContractorAudit audit) {
        return audit.getAuditType().getClassType().isPolicy() && !audit.isExpired();
    }

}
