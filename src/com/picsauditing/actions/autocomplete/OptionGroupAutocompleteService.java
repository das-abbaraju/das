package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;
import com.picsauditing.util.Strings;

public class OptionGroupAutocompleteService extends AutocompleteService<AuditOptionGroup> {
	@Autowired
	protected AuditOptionValueDAO auditQuestionOptionDAO;

	@Override
	protected Collection<AuditOptionGroup> getItems(String q) {
		if (!Strings.isEmpty(q)) {
			return auditQuestionOptionDAO.findOptionTypeWhere("o.name LIKE '%" + q + "%'");
		}

		return Collections.emptyList();
	}
}
