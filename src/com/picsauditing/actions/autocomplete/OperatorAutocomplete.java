package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public final class OperatorAutocomplete extends AutocompleteActionSupport<OperatorAccount> {

	@Autowired
	private OperatorAccountDAO dao;

	@Override
	protected Collection<OperatorAccount> getItems() {
		// HSE specific -- We want operators to search for sibling operators
		if (!permissions.isAdmin() && !permissions.isCorporate() && !permissions.isRequiresCompetencyReview()) {
			// TODO Non admin/corporate queries not supported yet
			return Collections.emptyList();
		}

		String corpPerms = "";
		if (permissions.isCorporate()) {
			corpPerms = String.format(" AND a.id IN (SELECT f.operator.id FROM Facility f "
					+ "WHERE f.corporate.id = %d)", permissions.getAccountId());
		}

		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				if (isSearchDigit())
					return dao.findWhere(true, "a.id LIKE '%" + Utilities.escapeQuotes(q) + "%'" + corpPerms);
				else
					return dao.findWhere(true, "a.name LIKE '%" + Utilities.escapeQuotes(q) + "%'" + corpPerms);
			}
		} else if (itemKeys.length > 0) {
			return dao.findWhere(true, "a.id IN (" + Strings.implode(itemKeys) + ")" + corpPerms, limit);
		}
		return Collections.emptyList();
	}
}
