package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

public class ContractorAutocompleteService extends AutocompleteService<ContractorAccount> {
	@Autowired
	private ContractorAccountDAO dao;

	@Override
	protected Collection<ContractorAccount> getItems(String q) {
		if (!Strings.isEmpty(q)) {
			if (isSearchDigit(q))
				return dao.findWhere("a.id LIKE '%" + Strings.escapeQuotes(q) + "%'");
			else
				return dao.findWhere("a.name LIKE '%" + Strings.escapeQuotes(q) + "%'");
		}

		return Collections.emptyList();
	}

	@Override
	public StringBuilder formatAutocomplete(ContractorAccount item) {
		StringBuilder sb = new StringBuilder();
		return sb.append(item.getId()).append("|").append(item.getName()).append("|").append(item.getName());
	}
}
