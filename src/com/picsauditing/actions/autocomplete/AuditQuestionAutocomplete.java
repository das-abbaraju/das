package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class AuditQuestionAutocomplete extends AutocompleteActionSupport<AuditQuestion> {

	@Autowired
	private AuditQuestionDAO auditQuestionDAO;

	@Override
	protected Collection<AuditQuestion> getItems() {
		if (isSearchDigit())
			return auditQuestionDAO.findWhere("t.id LIKE '" + q + "%'");
		else
			return auditQuestionDAO.findWhere("t.name LIKE '%" + q + "%'");
	}

	@Override
	public StringBuilder formatAutocomplete(AuditQuestion item) {
		// TODO-kyle: This should be in the getAutocompleteItem method for auditQuestions
		StringBuilder sb = new StringBuilder();

		// The ID is what we are really searching for
		sb.append(item.getId()).append("|");

		// Show ID if searching by ID
		if (isSearchDigit())
			sb.append("(").append(item.getId()).append(") ");

		// Show hierarchy of question
		sb.append(item.getAuditType().getName().toString()).append(" &gt; ");
		for (AuditCategory category : item.getCategory().getAncestors()) {
			sb.append(category.getName()).append(" &gt; ");
		}

		// Show question (escaped)
		sb.append(HtmlUtils.htmlEscape(item.getName()));

		return sb;
	}

}
