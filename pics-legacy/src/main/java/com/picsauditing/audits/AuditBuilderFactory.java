package com.picsauditing.audits;

import com.picsauditing.auditbuilder.AuditBuilder2;
import com.picsauditing.auditbuilder.AuditPercentCalculator2;
import com.picsauditing.featuretoggle.Features;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import org.springframework.beans.factory.annotation.Autowired;

public class AuditBuilderFactory {

    @Autowired
    AuditBuilder auditBuilder;
    @Autowired
    AuditPercentCalculator auditPercentCalculator;
    @Autowired
    AuditBuilder2 newAuditBuilder;
    @Autowired
    AuditPercentCalculator2 newAuditPercentCalculator;

    public void buildAudits(ContractorAccount contractorAccount) {
        if (newAuditBuilderEnabled()) {
            newAuditBuilder.buildAudits(contractorAccount.getId());
        } else {
            auditBuilder.buildAudits(contractorAccount);
        }
    }

    public void percentCalculateComplete(ContractorAudit contractorAudit) {
        if (newAuditPercentCalculatorEnabled()) {
            newAuditPercentCalculator.percentCalculateComplete(contractorAudit.getId(), true);
        } else {
            auditPercentCalculator.percentCalculateComplete(contractorAudit, true);
        }
    }

    public void recalculateCategories(ContractorAudit audit) {
        if (newAuditBuilderEnabled()) {
            newAuditBuilder.recalculateCategories(audit.getId());
        } else {
            auditBuilder.recalculateCategories(audit);
        }
    }

    private static boolean newAuditBuilderEnabled() {
        try {
            return Features.USE_NEW_AUDIT_BUILDER.isActive();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean newAuditPercentCalculatorEnabled() {
        try {
            return Features.USE_NEW_AUDIT_PERCENT_CALCULATOR.isActive();
        } catch (Exception e) {
            return false;
        }
    }

}
