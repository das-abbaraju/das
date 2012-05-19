package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

public final class AuditQuestionAutocompleteService extends AutocompleteService<AuditQuestion> {
	@Autowired
	private AuditQuestionDAO auditQuestionDAO;

	@Override
	protected Collection<AuditQuestion> getItems(String q) {
		if (isSearchDigit(q))
			return auditQuestionDAO.findWhere("t.id LIKE '" + q + "%'");
		else
			return auditQuestionDAO.findByTranslatableField(AuditQuestion.class, "name", Utilities.escapeHTML(q) + "%");
	}

}
