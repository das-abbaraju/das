package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.util.Strings;

public class OptionGroupAutocompleteService extends AbstractAutocompleteService<AuditOptionGroup> {
	
	@Autowired
	protected AuditOptionValueDAO auditQuestionOptionDAO;

	@Override
	protected Collection<AuditOptionGroup> getItems(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		return auditQuestionOptionDAO.findOptionTypeWhere("o.name LIKE '%" + search + "%'");
	}

	@Override
	protected Object getAutocompleteItem(AuditOptionGroup optionGroup) {
		return optionGroup.getAutocompleteValue();
	}

	@Override
	protected Object getAutocompleteValue(AuditOptionGroup optionGroup) {
		return optionGroup.getAutocompleteItem();
	}
}
