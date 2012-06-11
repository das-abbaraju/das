package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class GeneralContractorAutocomplete extends AutocompleteActionSupport<OperatorAccount> {
	@Autowired
	private OperatorAccountDAO dao;

	@Override
	protected Collection<OperatorAccount> getItems() {
		if (!permissions.isOperatorCorporate()) {
			return Collections.emptyList();
		}

		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				String visibleOperators = " AND a.id IN (";
				if (permissions.getLinkedGeneralContractors().size() > 0) {
					visibleOperators += Strings.implode(permissions.getLinkedGeneralContractors());
				} else {
					visibleOperators += Strings.implode(permissions.getLinkedClients());
				}

				visibleOperators += ")";

				if (visibleOperators.contains("()")) {
					visibleOperators = "";
				}

				if (isSearchDigit()) {
					return dao.findWhere(false, "a.id LIKE '%" + Strings.escapeQuotes(q) + "%'" + visibleOperators);
				} else {
					return dao.findWhere(false, "a.name LIKE '%" + Strings.escapeQuotes(q) + "%'" + visibleOperators);
				}
			}
		} else if (itemKeys.length > 0) {
			return dao.findWhere(false, "a.id IN (" + Strings.implodeForDB(itemKeys, ",") + ")", limit);
		}

		return Collections.emptyList();
	}
}
