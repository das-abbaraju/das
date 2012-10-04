package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

public class ContractorAutocompleteService extends AbstractAutocompleteService<ContractorAccount> {
	
	@Autowired
	private ContractorAccountDAO dao;

	@Override
	protected Collection<ContractorAccount> getItems(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		return dao.findWhere("a.name LIKE '%" + Strings.escapeQuotes(search) + "%'");
	}

	@Override
	protected Object getKey(ContractorAccount account) {
		return account.getId();
	}

	@Override
	protected Object getValue(ContractorAccount account, Permissions permissions) {
		return account.getName();
	}

}
