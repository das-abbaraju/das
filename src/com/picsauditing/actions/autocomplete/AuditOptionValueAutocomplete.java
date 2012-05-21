package com.picsauditing.actions.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.AuditOptionValue;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.util.Strings;

@Deprecated // This should be getting phased out with the release of Dynamic Report filters
@SuppressWarnings("serial")
public class AuditOptionValueAutocomplete extends AutocompleteActionSupport<Autocompleteable> {

	@Autowired
	protected AuditOptionValueDAO valueDAO;

	private AuditQuestion question;

	@Override
	protected Collection<Autocompleteable> getItems() {

		Collection<Autocompleteable> result = new ArrayList<Autocompleteable>();

		if (question != null && question.getOption() != null) {
			for (AuditOptionValue value : question.getOption().getValues()) {
				result.add(value);
			}
			return result;
		} else {
			if (!Strings.isEmpty(q)) {
				final String value = q;
				result.add(new Autocompleteable() {

					@Override
					public JSONObject toJSON() {
						return new JSONObject();
					}

					@Override
					public String getAutocompleteValue() {
						return value;
					}

					@Override
					public String getAutocompleteResult() {
						return value;
					}

					@Override
					public String getAutocompleteItem() {
						return value;
					}
				});
				return result;
			}
		}

		return Collections.<Autocompleteable> emptyList();
	}

	public AuditQuestion getQuestion() {
		return question;
	}

	public void setQuestion(AuditQuestion question) {
		this.question = question;
	}
}
