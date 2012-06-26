package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

@Deprecated // This should be getting phased out with the release of Dynamic Report filters
@SuppressWarnings("serial")
public class ContractorAutocomplete extends AutocompleteActionSupport<ContractorAccount> {

	@Autowired
	private ContractorAccountDAO dao;

	@Override
	protected Collection<ContractorAccount> getItems() {
		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				if (isSearchDigit())
					return dao.findWhere("a.id LIKE '%" + Strings.escapeQuotes(q) + "%'");
				else
					return dao.findWhere("a.name LIKE '%" + Strings.escapeQuotes(q) + "%'");
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
