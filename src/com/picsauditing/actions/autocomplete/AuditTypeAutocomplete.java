package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;

@Deprecated // This should be getting phased out with the release of Dynamic Report filters
@SuppressWarnings("serial")
public final class AuditTypeAutocomplete extends AutocompleteActionSupport<AuditType> {

	@Autowired
	private AuditTypeDAO auditTypeDAO;

	@Override
	protected Collection<AuditType> getItems() {
		if (isSearchDigit())
			return auditTypeDAO.findWhere("t.id LIKE '" + q + "%'");
		else
			return auditTypeDAO.findByTranslatableField(AuditType.class, "name", Utilities.escapeHTML(q) + "%");
	}

}
