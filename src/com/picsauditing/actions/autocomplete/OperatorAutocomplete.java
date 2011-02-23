package com.picsauditing.actions.autocomplete;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class OperatorAutocomplete extends AutocompleteActionSupport<OperatorAccount> {

	private OperatorAccountDAO dao;

	public OperatorAutocomplete(OperatorAccountDAO dao) {
		this.dao = dao;
	}

	@Override
	protected void findItems() {
		loadPermissions();
		if (!permissions.isAdmin()) {
			// TODO Non admin queries not supported yet
			return;
		}
		
		if (isSearchDigit())
			items = dao.findWhere(true, "a.id LIKE '%" + Utilities.escapeQuotes(q) + "%'");
		else
			items = dao.findWhere(true, "a.name LIKE '%" + Utilities.escapeQuotes(q) + "%'");
	}
}
