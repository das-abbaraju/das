package com.picsauditing.models.audits;

import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.jpa.entities.*;

import java.util.HashSet;
import java.util.Set;

public class InsurancePolicySuggestionCalculator {
    public static void calculateSuggestionForAllPolicies(ContractorAccount contractor, FlagDataCalculator flagDataCalculator){
        for (ContractorAudit audit : contractor.getAudits()) {
            if (nonExpiredInsurancePolicy(audit)) {
                calculateSuggestionForPolicy(contractor, flagDataCalculator, audit);
            }
        }
    }

    public static void calculateSuggestionForPolicy(ContractorAccount contractor, FlagDataCalculator flagDataCalculator, ContractorAudit audit) {
        for (ContractorAuditOperator cao : audit.getOperators())
            if (cao.getStatus().after(AuditStatus.Incomplete)) {
                Set<FlagData> flagDataSet = gatherFlagDataFromAppropriateOperators(contractor, cao);

                FlagColor flagColor = flagDataCalculator.calculateCaoStatus(audit.getAuditType(), flagDataSet);
                cao.setFlag(flagColor);
        }
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
