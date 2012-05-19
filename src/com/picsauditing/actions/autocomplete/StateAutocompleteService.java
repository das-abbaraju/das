package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.StateDAO;
import com.picsauditing.jpa.entities.Autocompleteable;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.Strings;

public class StateAutocompleteService extends AutocompleteService<Autocompleteable> {
	@Autowired
	protected StateDAO stateDAO;

	@Override
	protected Collection<Autocompleteable> getItems(String q) {
		Collection<Autocompleteable> result = new HashSet<Autocompleteable>();
		if (!Strings.isEmpty(q)) {
			if (q.length() == 2) {
				// search both iso and translated fields for the 2 letter
				// combinations
				List<State> stateList = stateDAO.findWhere("isoCode = '" + Strings.escapeQuotes(q) + "'");
				result.addAll(stateList);

				stateList = stateDAO.findByTranslatableField(State.class, "%" + Strings.escapeQuotes(q) + "%");

				result.addAll(stateList);
				return result;
			} else {
				// any more or less characters, then search only through
				// translations
				List<State> stateList = stateDAO.findByTranslatableField(State.class, "%" + Strings.escapeQuotes(q)
						+ "%");

				result.addAll(stateList);

				return result;
			}
		}

		return Collections.emptyList();
	}
}