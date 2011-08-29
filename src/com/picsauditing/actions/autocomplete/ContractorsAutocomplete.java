package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public final class ContractorsAutocomplete extends AutocompleteActionSupport<ContractorAccount> {

	@Autowired
	private ContractorAccountDAO dao;

	@Override
	protected Collection<ContractorAccount> getItems() {
		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				if (isSearchDigit())
					return dao.findWhere("a.id LIKE '%" + Utilities.escapeQuotes(q) + "%'");
				else
					return dao.findWhere("a.name LIKE '%" + Utilities.escapeQuotes(q) + "%'");
			}
		} else if (itemKeys.length > 0) {
			return dao.findWhere("a.id IN (" + Strings.implodeForDB(itemKeys, ",") + ")");
		}
		return Collections.emptyList();
	}

	@Override
	public StringBuilder formatAutocomplete(ContractorAccount item) {
		StringBuilder sb = new StringBuilder();
		return sb.append(item.getId()).append("|").append(item.getName()).append("|").append(item.getName());
	}
}