package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AuditCategoryRuleBuilder {
    private AuditCategoryRule auditCategoryRule = new AuditCategoryRule();

    public AuditCategoryRule build() {
        return auditCategoryRule;
    }

    public AuditCategoryRuleBuilder include() {
        auditCategoryRule.setInclude(true);
        return this;
    }

    public AuditCategoryRuleBuilder operator(OperatorAccount operator) {
        auditCategoryRule.setOperatorAccount(operator);
        return this;
    }

    public AuditCategoryRuleBuilder category(AuditCategory category) {
        auditCategoryRule.setAuditCategory(category);
        return this;
    }
}
