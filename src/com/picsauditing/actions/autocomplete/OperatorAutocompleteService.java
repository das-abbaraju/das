package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

public final class OperatorAutocompleteService extends AutocompleteService<OperatorAccount> {
	@Autowired
	private OperatorAccountDAO dao;

	protected Collection<OperatorAccount> getItems(String q, Permissions permissions) {
		// HSE specific -- We want operators to search for sibling operators
		if (!permissions.isAdmin() && !permissions.isCorporate() && !permissions.isRequiresCompetencyReview()
				&& !permissions.isGeneralContractor()) {
			// TODO Non admin/corporate queries not supported yet
			return Collections.emptyList();
		}

		String corpPerms = "";
		if (permissions.isCorporate()) {
			corpPerms = String.format(" AND a.id IN (SELECT f.operator.id FROM Facility f "
					+ "WHERE f.corporate.id = %d)", permissions.getAccountId());
		} else if (permissions.isGeneralContractor()) {
			corpPerms = " AND a.id != " + permissions.getAccountId();
		}

		boolean showCorporates = !permissions.isGeneralContractor();

		if (!Strings.isEmpty(q)) {
			if (isSearchDigit(q))
				return dao.findWhere(showCorporates, "a.id LIKE '%" + Strings.escapeQuotes(q) + "%'" + corpPerms,
						permissions);
			else
				return dao.findWhere(showCorporates, "a.name LIKE '%" + Strings.escapeQuotes(q) + "%'" + corpPerms,
						permissions);
		}

		return Collections.emptyList();
	}
	
	@SuppressWarnings("unchecked")
	public final JSONObject json(String q, Permissions p) {
		JSONObject json = new JSONObject();

		JSONArray result = new JSONArray();
		for (OperatorAccount item : getItems(q, p)) {
			result.add(formatJson(item));
		}

		json.put("result", result);

		return json;
	}

	@Override
	protected Collection<OperatorAccount> getItems(String q) {
		return null;
	}
}
