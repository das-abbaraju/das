package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;

public final class AuditTypeAutocompleteService extends AutocompleteService<AuditType> {
	@Autowired
	private AuditTypeDAO auditTypeDAO;

	@Override
	protected Collection<AuditType> getItems(String q) {
		if (isSearchDigit(q))
			return auditTypeDAO.findWhere("t.id LIKE '" + q + "%'");
		else
			return auditTypeDAO.findByTranslatableField(AuditType.class, "name", Utilities.escapeHTML(q) + "%");
	}

}
