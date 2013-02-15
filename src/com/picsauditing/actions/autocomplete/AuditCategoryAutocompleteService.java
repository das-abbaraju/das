package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.math.NumberUtils;
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
	protected Collection<AuditCategory> getItemsForSearch(String search, Permissions permissions) {
		return auditCategoryDAO.findByTranslatableField(AuditCategory.class, "", "name",
				"%" + Strings.escapeQuotes(search) + "%", permissions.getLocale(), RESULT_SET_LIMIT);
	}

	@Override
	protected Object getKey(AuditCategory auditCategory) {
		return auditCategory.getId();
	}

	@Override
	protected Object getValue(AuditCategory auditCategory, Permissions permissions) {
		return I18nCache.getInstance().getText(auditCategory.getI18nKey() + ".name", permissions.getLocale());
	}

	@Override
	protected Collection<AuditCategory> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int auditCategoryId = NumberUtils.toInt(searchKey);
		if (auditCategoryId == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(auditCategoryDAO.find(auditCategoryId));
	}
}
