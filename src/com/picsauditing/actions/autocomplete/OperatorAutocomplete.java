package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@Deprecated // This should be getting phased out with the release of Dynamic Report filters
@SuppressWarnings("serial")
public final class OperatorAutocomplete extends AutocompleteActionSupport<OperatorAccount> {

	@Autowired
	private OperatorAccountDAO dao;

	@Override
	protected Collection<OperatorAccount> getItems() {
		// HSE specific -- We want operators to search for sibling operators
		if (!permissions.isAdmin() && !permissions.isCorporate() && !permissions.isRequiresCompetencyReview()
				&& !permissions.isGeneralContractor() && permissions.getLinkedGeneralContractors().isEmpty()) {
			// TODO Non admin/corporate queries not supported yet
			return Collections.emptyList();
		}

		if (!permissions.getLinkedGeneralContractors().isEmpty()) {
			return dao.findWhere(false, "a.id IN (" + Strings.implode(permissions.getLinkedGeneralContractors()) + ")");
		}

		String corpPerms = "";
		if (permissions.isCorporate()) {
			corpPerms = String.format(" AND a.id IN (SELECT f.operator.id FROM Facility f "
					+ "WHERE f.corporate.id = %d)", permissions.getAccountId());
		}

		boolean showCorporates = !permissions.isGeneralContractor();

		if (itemKeys == null) {
			if (!Strings.isEmpty(q)) {
				if (isSearchDigit())
					return dao.findWhere(showCorporates, "a.id LIKE '%" + Strings.escapeQuotes(q) + "%'" + corpPerms,
							permissions);
				else
					return dao.findWhere(showCorporates, "a.name LIKE '%" + Strings.escapeQuotes(q) + "%'" + corpPerms,
							permissions);
			}
		} else if (itemKeys.length > 0) {
			return dao.findWhere(true, "a.id IN (" + Strings.implodeForDB(itemKeys, ",") + ")" + corpPerms, limit);
		}

		return Collections.emptyList();
	}
}
