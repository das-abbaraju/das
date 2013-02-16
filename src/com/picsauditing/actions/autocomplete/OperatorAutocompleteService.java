package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

public final class OperatorAutocompleteService extends AbstractAutocompleteService<OperatorAccount> {

	@Autowired
	private OperatorAccountDAO dao;

	protected Collection<OperatorAccount> getItemsForSearch(String search, Permissions permissions) {
		if (Strings.isEmpty(search) || noPermissionsToSearch(permissions)) {
			return Collections.emptyList();
		}

		boolean showCorporates = !permissions.isGeneralContractor();

		return dao.findWhere(showCorporates, "a.name LIKE '%" + Strings.escapeQuotes(search) + "%'"
				+ buildCorporatePermissions(permissions), permissions, RESULT_SET_LIMIT);
	}

	private boolean noPermissionsToSearch(Permissions permissions) {
		// HSE specific -- We want operators to search for sibling operators
		// TODO Non admin/corporate queries not supported yet

		return !permissions.isAdmin() && !permissions.isCorporate() && !permissions.isRequiresCompetencyReview()
				&& !permissions.isGeneralContractor();
	}

	private String buildCorporatePermissions(Permissions permissions) {
		if (permissions.isCorporate()) {
			return String.format(" AND a.id IN (SELECT f.operator.id FROM Facility f "
					+ "WHERE f.corporate.id = %d)", permissions.getAccountId());
		} else if (permissions.isGeneralContractor()) {
			return " AND a.id != " + permissions.getAccountId();
		}

		return Strings.EMPTY_STRING;
	}

	@Override
	protected Object getKey(OperatorAccount clientSite) {
		return clientSite.getId();
	}

	@Override
	protected Object getValue(OperatorAccount clientSite, Permissions permissions) {
		return clientSite.getName();
	}

	@Override
	protected Collection<OperatorAccount> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int operatorId = NumberUtils.toInt(searchKey);
		if (operatorId == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(dao.find(operatorId));

	}
}
