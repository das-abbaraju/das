package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

public class OperatorTagAutocompleteService extends AbstractAutocompleteService<OperatorTag> {

	@Autowired
	private OperatorTagDAO operatorTagDAO;

	@Override
	protected Collection<OperatorTag> getItemsForSearch(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		String operatorIDs = permissions.getAccountIdString();

		if (permissions.isOperator()) {
			operatorIDs += "," + Strings.implode(permissions.getCorporateParent());
		}

		String permissionsQuery = "AND opID IN (" + operatorIDs + ")";

		if (permissions.isPicsEmployee()) {
			permissionsQuery = "";
		}

		List<OperatorTag> tags = operatorTagDAO.findWhere(OperatorTag.class, " t.tag LIKE '%" +
				Strings.escapeQuotesAndSlashes(search) + "%' " + permissionsQuery, RESULT_SET_LIMIT);
		if (CollectionUtils.isEmpty(tags)) {
			return Collections.emptyList();
		}

		return tags;
	}

	@Override
	protected Object getKey(OperatorTag operatorTag) {
		return operatorTag.getId();
	}

	@Override
	protected Object getValue(OperatorTag operatorTag, Permissions permissions) {
		return operatorTag.getTag();
	}

	@Override
	protected Collection<OperatorTag> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int operatorTagId = NumberUtils.toInt(searchKey);
		if (operatorTagId == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(operatorTagDAO.find(operatorTagId));
	}
}
