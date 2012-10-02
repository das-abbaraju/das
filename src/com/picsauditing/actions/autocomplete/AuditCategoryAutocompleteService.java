package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.util.Strings;

public class AuditCategoryAutocompleteService extends AbstractAutocompleteService<AuditCategory> {

	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;

	@Override
	protected Collection<AuditCategory> getItems(String search, Permissions permissions) {
		return auditCategoryDAO.findByTranslatableField(AuditCategory.class, "name", "%" + Strings.escapeQuotes(search)
				+ "%");
	}

	@Override
	protected Object getAutocompleteItem(AuditCategory auditCategory) {
		return auditCategory.getAutocompleteItem();
	}

	@Override
	protected Object getAutocompleteValue(AuditCategory auditCategory) {
		return auditCategory.getAutocompleteValue();
	}
}
