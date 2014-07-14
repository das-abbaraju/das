package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.AuditCategory;
import com.picsauditing.auditbuilder.service.AuditService;

import java.util.Date;

public class ContractorAuditCategories {
    public static boolean isCategoryEffective(AuditCategory category, Date effectiveDate) {
        if (effectiveDate == null)
            return AuditService.isCurrent(category);
        return AuditService.isCurrent(category, effectiveDate);
    }
}