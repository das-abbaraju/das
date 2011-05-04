package com.picsauditing.actions.autocomplete;

import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditOptionType;

@SuppressWarnings("serial")
public class OptionTypeAutocomplete extends AutocompleteActionSupport<AuditOptionType> {
	protected AuditQuestionDAO auditQuestionDAO;

	public OptionTypeAutocomplete(AuditQuestionDAO auditQuestionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
	}

	@Override
	protected void findItems() {
		items = auditQuestionDAO.findOptionTypeWhere("o.name LIKE '%" + q + "%'");
	}
}
