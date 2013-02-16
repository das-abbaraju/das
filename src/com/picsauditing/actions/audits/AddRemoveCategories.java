package com.picsauditing.actions.audits;

import java.util.Collections;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also
 * allows users to change the status of an audit.
 */
@SuppressWarnings("serial")
public class AddRemoveCategories extends AuditActionSupport {

	@RequiredPermission(value = OpPerms.AuditEdit)
	public String execute() throws Exception {
		this.findConAudit();
		Collections.sort(conAudit.getCategories());

		if (button != null) {
			AuditCategory auditCategory = (AuditCategory) catDataDao.find(AuditCategory.class, categoryID);
			
			if (auditCategory == null)
				return SUCCESS;
			
			if ("IncludeCategory".equals(button)) {
				addManuallyAddedCategory(auditCategory);
			}

			if ("UnincludeCategory".equals(button)) {
				hideCategory(auditCategory);
			}

			conAudit.setLastRecalculation(null);
			contractor.incrementRecalculation();

			return SUCCESS;
		}

		return SUCCESS;
	}

	private void addManuallyAddedCategory(AuditCategory auditCategory) {
		AuditCatData auditCatData = getCatData(auditCategory);
		if (auditCatData == null) {
			auditCatData = new AuditCatData();
			auditCatData.setAuditColumns(permissions);
			auditCatData.setAudit(conAudit);
			conAudit.getCategories().add(auditCatData);
		}
		auditCatData.setApplies(true);
		auditCatData.setOverride(true);
		for (AuditCategory childCategory : auditCatData.getCategory().getChildren()) {
			if (auditCategory.getId() != childCategory.getId()) {
				addManuallyAddedCategory(childCategory);
			}
		}
		auditDao.save(auditCatData);
	}

	private AuditCatData getCatData(AuditCategory auditCategory) {
		for (AuditCatData auditCatData : conAudit.getCategories()) {
			if (auditCatData.getCategory().equals(auditCategory))
				return auditCatData;
		}
		
		return null;
	}

	private void hideCategory(AuditCategory auditCategory) {
		AuditCatData auditCatData = getCatData(auditCategory);
		if (auditCatData != null) {
			auditCatData.setApplies(false);
			auditCatData.setOverride(true);
			for (AuditCategory childCategory : auditCatData.getCategory().getChildren()) {
				if (auditCategory.getId() != childCategory.getId()) {
					hideCategory(childCategory);
				}
			}
		}
		auditDao.save(auditCatData);
	}
}