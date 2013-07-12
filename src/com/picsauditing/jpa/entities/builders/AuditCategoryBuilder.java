package com.picsauditing.jpa.entities.builders;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;

public class AuditCategoryBuilder {
    private AuditCategory auditCategory = new AuditCategory();
    public AuditCategoryBuilder auditType(AuditType auditType) {
        auditCategory.setAuditType(auditType);
        return this;
    }

    public AuditCategory build() {
        return auditCategory;
    }
}
