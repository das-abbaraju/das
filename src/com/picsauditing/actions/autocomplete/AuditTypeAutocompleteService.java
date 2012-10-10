package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.List;

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
		String permissionWhere = "";

		if (permissions.isOperatorCorporate())
			permissionWhere += "t.canOperatorView = 1 AND t.id IN ("
					+ Strings.implode(permissions.getVisibleAuditTypes()) + ")";

		List<AuditType> permittedAuditTypes = auditTypeDAO.findByTranslatableField(AuditType.class, permissionWhere, "name",
				"%", permissions.getLocale(), RESULT_SET_LIMIT);
		System.out.println(permittedAuditTypes);

		String value = "%" + Strings.escapeQuotes(search) + "%";
		List<AuditType> auditTypes = auditTypeDAO.findByTranslatableField(AuditType.class, permissionWhere, "name",
				value, permissions.getLocale(), RESULT_SET_LIMIT);
		System.out.println(auditTypes);
		return auditTypes;
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
