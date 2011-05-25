package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public final class StateQuestionAutocomplete extends AutocompleteActionSupport<AuditQuestion> {

	@Autowired
	protected AuditQuestionDAO auditQuestionDAO;

	protected String extraArgs;

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<AuditQuestion> getItems() {
		if (!Strings.isEmpty(extraArgs) && !Strings.isEmpty(q)) {
			return (Collection<AuditQuestion>) auditQuestionDAO.findWhere(AuditQuestion.class, "t.option.uniqueCode = '"
					+ Utilities.escapeQuotes(extraArgs) + "' AND t.name LIKE '" + Utilities.escapeQuotes(q) + "%'",
					limit);
		}
		return Collections.emptyList();
	}

	public String getExtraArgs() {
		return extraArgs;
	}

	public void setExtraArgs(String extraArgs) {
		this.extraArgs = extraArgs;
	}

}
