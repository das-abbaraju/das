package com.picsauditing.actions.autocomplete;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.util.Strings;

public class OptionGroupAutocompleteService extends AbstractAutocompleteService<AuditOptionGroup> {

	@Autowired
	protected AuditOptionValueDAO auditQuestionOptionDAO;

	@Override
	protected Collection<AuditOptionGroup> getItemsForSearch(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		return auditQuestionOptionDAO.findOptionTypeWhere("o.name LIKE '%" + search + "%'", RESULT_SET_LIMIT);
	}

	@Override
	protected Object getKey(AuditOptionGroup optionGroup) {
		return optionGroup.getId();
	}

	@Override
	protected Object getValue(AuditOptionGroup optionGroup, Permissions permissions) {
		return optionGroup.getName();
	}

	@Override
	protected Collection<AuditOptionGroup> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int optionGroupId = NumberUtils.toInt(searchKey);
		if (optionGroupId == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(auditQuestionOptionDAO.findOptionGroup(optionGroupId));
	}
}
