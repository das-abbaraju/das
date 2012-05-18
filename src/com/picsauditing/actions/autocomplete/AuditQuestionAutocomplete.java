package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;

@Deprecated // This should be getting phased out with the release of Dynamic Report filters
@SuppressWarnings("serial")
public final class AuditQuestionAutocomplete extends AutocompleteActionSupport<AuditQuestion> {

	@Autowired
	private AuditQuestionDAO auditQuestionDAO;

	@Override
	protected Collection<AuditQuestion> getItems() {
		if (isSearchDigit())
			return auditQuestionDAO.findWhere("t.id LIKE '" + q + "%'");
		else
			return auditQuestionDAO.findByTranslatableField(AuditQuestion.class, "name", Utilities.escapeHTML(q) + "%");
	}

}
