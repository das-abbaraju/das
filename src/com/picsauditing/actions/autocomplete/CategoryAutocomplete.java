package com.picsauditing.actions.autocomplete;

import java.util.Iterator;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.jpa.entities.AuditCategory;

@SuppressWarnings("serial")
public class CategoryAutocomplete extends AutocompleteActionSupport<AuditCategory> {

	protected AuditCategoryDAO auditCategoryDAO;
	protected Integer auditTypeID;

	public CategoryAutocomplete(AuditCategoryDAO auditCategoryDAO) {
		this.auditCategoryDAO = auditCategoryDAO;
	}

	@Override
	protected void findItems() {
		String where = "";
		if (auditTypeID != null && auditTypeID > 0)
			where = "t.auditType.id =" + auditTypeID + " AND ";
		if (isSearchDigit())
			items = auditCategoryDAO.findWhere(where + "t.id LIKE '" + q + "%'");
		else
			items = auditCategoryDAO.findWhere(where + "t.name LIKE '%" + q + "%'");
	}

	@Override
	protected void createOutput() {
		for (AuditCategory category : items) {
			// The ID of the category we are searching for
			outputBuffer.append(category.getId()).append("|");

			// The display of the category
			if (isSearchDigit())
				outputBuffer.append("(").append(category.getId()).append(") ");
			outputBuffer.append(category.getAuditType().getAuditName()).append(" &gt; ");

			Iterator<AuditCategory> ancestors = category.getAncestors().iterator();
			while (ancestors.hasNext()) {
				outputBuffer.append(ancestors.next().getName());
				if (ancestors.hasNext())
					outputBuffer.append(" &gt; ");
			}

			outputBuffer.append("\n");
		}
	}

	public Integer getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(Integer auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

}
