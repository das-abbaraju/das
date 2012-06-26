package com.picsauditing.actions.autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AuditOptionValueDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.util.Strings;

public class OptionValueAutocompleteService extends AutocompleteService<Autocompleteable> {
	@Autowired
	protected AuditOptionValueDAO valueDAO;

	@Override
	protected Collection<Autocompleteable> getItems(String q) {
		Collection<Autocompleteable> result = new ArrayList<Autocompleteable>();

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

		return Collections.<Autocompleteable> emptyList();
	}
}
