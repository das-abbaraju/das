package com.picsauditing.auditBuilder;

import java.util.*;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;

public class ContractorAuditCategories {
	public static TreeMap<AuditCategory, AuditCatData> getApplicableCategories(Permissions permissions,
			Set<AuditCategory> requiredCategories, List<AuditCatData> sortedCats) {
		TreeMap<AuditCategory, AuditCatData> categories = new TreeMap<AuditCategory, AuditCatData>();

		for (AuditCatData auditCatData : sortedCats) {
			if (isCategoryVisible(permissions, requiredCategories, auditCatData))
				categories.put(auditCatData.getCategory(), auditCatData);
		}

		return categories;
	}

	private static boolean isCategoryVisible(Permissions permissions, Set<AuditCategory> requiredCategories,
			AuditCatData auditCatData) {

		if (!auditCatData.isApplies())
			return false;

        if (!isCategoryEffective(auditCatData.getCategory(), auditCatData.getAudit().getEffectiveDate())) {
            return false;
        }
		if (permissions.isContractor())
			return true;

		AuditCategory category = auditCatData.getCategory();

		if (permissions.isAdmin() && !permissions.isOperatorCorporate()) {
			if (category.requiresViewFullPQFPermission()) {
				return permissions.hasPermission(OpPerms.ViewFullPQF);
			}
		}

		if (requiredCategories == null)
			return true;
		
		if (requiredCategories.contains(category)) {
			if (category.requiresViewFullPQFPermission()) {
				return permissions.hasPermission(OpPerms.ViewFullPQF);
			}
			
			return true;
		}

		return false;
	}

    public static boolean isCategoryEffective(AuditCategory category, Date effectiveDate) {
        if (effectiveDate == null)
            return category.isCurrent();
        return category.isCurrent(effectiveDate);
    }

}
