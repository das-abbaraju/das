package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class OperatorAutocomplete extends AutocompleteActionSupport<OperatorAccount> {

	@Autowired
	private OperatorAccountDAO dao;

	@Override
	protected Collection<OperatorAccount> getItems() {
		if (!permissions.isAdmin()) {
			// TODO Non admin queries not supported yet
			return Collections.<OperatorAccount> emptyList();
		}

		if (isSearchDigit())
			return dao.findWhere(true, "a.id LIKE '%" + Utilities.escapeQuotes(q) + "%'");
		else
			return dao.findWhere(true, "a.name LIKE '%" + Utilities.escapeQuotes(q) + "%'");
	}
}
