package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
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
	protected Object getKey(AuditCategory auditCategory) {
		return auditCategory.getId();
	}

	@Override
	protected Object getValue(AuditCategory auditCategory, Permissions permissions) {
		return I18nCache.getInstance().getText(auditCategory.getI18nKey(), permissions.getLocale());
	}
}
