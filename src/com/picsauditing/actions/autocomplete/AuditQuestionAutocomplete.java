package com.picsauditing.actions.autocomplete;

import org.springframework.web.util.HtmlUtils;

import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;

@SuppressWarnings("serial")
public class AuditQuestionAutocomplete extends AutocompleteActionSupport<AuditQuestion> {

	private AuditQuestionDAO auditQuestionDAO;

	public AuditQuestionAutocomplete(AuditQuestionDAO auditQuestionDAO) {
		this.auditQuestionDAO = auditQuestionDAO;
	}

	@Override
	protected void findItems() {
		if (isSearchDigit())
			items = auditQuestionDAO.findWhere("t.id LIKE '" + q + "%'");
		else
			items = auditQuestionDAO.findWhere("t.name LIKE '%" + q + "%'");
	}

	@Override
	protected void createOutput() {
		for (AuditQuestion question : items) {
			// The ID is what we are really searching for
			outputBuffer.append(question.getId()).append("|");
			
			// Show ID if searching by ID
			if (isSearchDigit())
				outputBuffer.append("(").append(question.getId()).append(") ");

			// Show hierarchy of question
			outputBuffer.append(question.getAuditType().getAuditName()).append(" &gt; ");
			for (AuditCategory category : question.getCategory().getAncestors()) {
				outputBuffer.append(category.getName()).append(" &gt; ");
			}

			// Show question (escaped)
			outputBuffer.append(HtmlUtils.htmlEscape(question.getName())).append("\n");
		}
	}
}
