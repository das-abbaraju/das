package com.picsauditing.auditbuilder;

import com.picsauditing.auditbuilder.entities.AuditCategory;
import com.picsauditing.auditbuilder.service.AuditService;

import java.util.Date;

public class ContractorAuditCategories {
//	public static TreeMap<AuditCategory, AuditCatData> getApplicableCategories(Permissions permissions,
//			Set<AuditCategory> requiredCategories, List<AuditCatData> sortedCats) {
//		TreeMap<AuditCategory, AuditCatData> categories = new TreeMap<>();
//
//		for (AuditCatData auditCatData : sortedCats) {
//			if (isCategoryVisible(permissions, requiredCategories, auditCatData))
//				categories.put(auditCatData.getCategory(), auditCatData);
//		}
//
//		return categories;
//	}
//
//	private static boolean isCategoryVisible(Permissions permissions, Set<AuditCategory> requiredCategories,
//			AuditCatData auditCatData) {
//
//		if (!auditCatData.isApplies())
//			return false;
//
//        if (!isCategoryEffective(auditCatData.getCategory(), auditCatData.getAudit().getEffectiveDate())) {
//            return false;
//        }
//		if (permissions.isContractor())
//			return true;
//
//		AuditCategory category = auditCatData.getCategory();
//
//		if (permissions.isAdmin() && !permissions.isOperatorCorporate()) {
//			if (AuditService.requiresViewFullPQFPermission(category)) {
//				return permissions.hasPermission(OpPerms.ViewFullPQF);
//			}
//		}
//
//		if (requiredCategories == null)
//			return true;
//
//		if (requiredCategories.contains(category)) {
//			if (AuditService.requiresViewFullPQFPermission(category)) {
//				return permissions.hasPermission(OpPerms.ViewFullPQF);
//			}
//
//			return true;
//		}
//
//		return false;
//	}
//
    public static boolean isCategoryEffective(AuditCategory category, Date effectiveDate) {
        if (effectiveDate == null)
            return AuditService.isCurrent(category);
        return AuditService.isCurrent(category, effectiveDate);
    }

}
