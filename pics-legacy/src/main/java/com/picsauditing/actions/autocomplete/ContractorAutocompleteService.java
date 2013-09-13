package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.util.Strings;

public class ContractorAutocompleteService extends AbstractAutocompleteService<ContractorAccount> {

	@Autowired
	private ContractorAccountDAO dao;

	@Override
	protected Collection<ContractorAccount> getItemsForSearch(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		return dao.findWhere("a.name LIKE '%" + Strings.escapeQuotes(search) + "%'", RESULT_SET_LIMIT);
	}

	@Override
	protected Object getKey(ContractorAccount account) {
		return account.getId();
	}

	@Override
	protected Object getValue(ContractorAccount account, Permissions permissions) {
		return account.getName();
	}

	@Override
	protected Collection<ContractorAccount> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int contractorId = NumberUtils.toInt(searchKey);
		if (contractorId == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(dao.find(contractorId));
	}

}
