package com.picsauditing.actions.autocomplete;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionGroup;

@SuppressWarnings("serial")
public class OptionGroupAutocomplete extends AutocompleteActionSupport<AuditOptionGroup> {
	@Autowired
	protected AuditOptionValueDAO auditQuestionOptionDAO;

	@Override
	protected void findItems() {
		items = auditQuestionOptionDAO.findOptionTypeWhere("o.name LIKE '%" + q + "%'");
	}
}
