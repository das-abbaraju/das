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
		return auditTypeDAO.findByTranslatableField(AuditType.class, "", "name", 
				Strings.escapeQuotes(search) + "%", permissions.getLocale(), RESULT_SET_LIMIT);
	}

	@Override
	protected Object getKey(AuditType auditType) {
		return auditType.getId();
	}

	@Override
	protected Object getValue(AuditType auditType, Permissions permissions) {
		return auditType.getName().toString(permissions.getLocale());
	}

}
