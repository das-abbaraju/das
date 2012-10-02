package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.Strings;

public final class AuditTypeAutocompleteService extends AbstractAutocompleteService<AuditType> {
	
	@Autowired
	private AuditTypeDAO auditTypeDAO;

	@Override
	protected Collection<AuditType> getItems(String search, Permissions permissions) {
		return auditTypeDAO.findByTranslatableField(AuditType.class, "name", Strings.escapeQuotes(search) + "%");
	}

	@Override
	protected Object getAutocompleteItem(AuditType auditType) {
		return auditType.getAutocompleteItem();
	}

	@Override
	protected Object getAutocompleteValue(AuditType auditType) {
		return auditType.getAutocompleteValue();
	}

}
