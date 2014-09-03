package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.DocumentCategory;
import com.picsauditing.auditbuilder.service.DocumentUtilityService;

import java.util.Date;

public class ContractorAuditCategories {
    public static boolean isCategoryEffective(DocumentCategory category, Date effectiveDate) {
        if (effectiveDate == null)
            return DocumentUtilityService.isCurrent(category);
        return DocumentUtilityService.isCurrent(category, effectiveDate);
    }
}