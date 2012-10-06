package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.util.Strings;

public final class AuditQuestionAutocompleteService extends AbstractAutocompleteService<AuditQuestion> {
	
	@Autowired
	private AuditQuestionDAO auditQuestionDAO;

	@Override
	protected Collection<AuditQuestion> getItems(String search, Permissions permissions) {
		return auditQuestionDAO.findByTranslatableField(AuditQuestion.class, "name", Strings.escapeQuotes(search) + "%");
	}

	@Override
	protected Object getKey(AuditQuestion question) {
		return question.getId();
	}

	@Override
	protected Object getValue(AuditQuestion question, Permissions permissions) {
		return I18nCache.getInstance().getText(question.getI18nKey() + ".name", permissions.getLocale());
	}

}
