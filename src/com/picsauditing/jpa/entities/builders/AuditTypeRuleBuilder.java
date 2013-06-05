package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;

public class AuditTypeRuleBuilder {
    private AuditTypeRule auditTypeRule = new AuditTypeRule();

    public AuditTypeRule build() {
        return auditTypeRule;
    }

    public AuditTypeRuleBuilder exclude() {
        auditTypeRule.setInclude(false);
        return this;
    }

    public AuditTypeRuleBuilder include() {
        auditTypeRule.setInclude(true);
        return this;
    }

    public AuditTypeRuleBuilder auditType(AuditType auditType) {
        auditTypeRule.setAuditType(auditType);
        return this;
    }
}
