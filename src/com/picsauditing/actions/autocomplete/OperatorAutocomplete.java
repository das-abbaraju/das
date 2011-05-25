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
		if (!permissions.isAdmin()) {
			// TODO Non admin queries not supported yet
			return Collections.emptyList();
		}

		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				if (isSearchDigit())
					return dao.findWhere(true, "a.id LIKE '%" + Utilities.escapeQuotes(q) + "%'");
				else
					return dao.findWhere(true, "a.name LIKE '%" + Utilities.escapeQuotes(q) + "%'");
			}
		} else if (itemKeys.length > 0) {
			return dao.findWhere(true, "a.id IN (" + Strings.implode(itemKeys) + ")", limit);
		}
		return Collections.emptyList();
	}
}
