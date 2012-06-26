package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.util.Strings;

public class AuditCategoryAutocompleteService extends AutocompleteService<AuditCategory> {

	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;

	@Override
	protected Collection<AuditCategory> getItems(String q) {
		String where = "";
		if (isSearchDigit(q))
			return auditCategoryDAO.findWhere(where + "t.id LIKE '" + Strings.escapeQuotes(q) + "%'");
		else
			return auditCategoryDAO.findByTranslatableField(AuditCategory.class, "name",
					"%" + Strings.escapeQuotes(q) + "%");
	}

	@Override
	public StringBuilder formatAutocomplete(AuditCategory item) {
		StringBuilder outputBuffer = new StringBuilder();
		// The ID of the category we are searching for
		outputBuffer.append(item.getId()).append("|");

		// The display of the category
		outputBuffer.append(item.getAuditType().getName().toString()).append(" &gt; ");

		Iterator<AuditCategory> ancestors = item.getAncestors().iterator();
		while (ancestors.hasNext()) {
			outputBuffer.append(ancestors.next().getName());
			if (ancestors.hasNext())
				outputBuffer.append(" &gt; ");
		}

		return outputBuffer;
	}

}
