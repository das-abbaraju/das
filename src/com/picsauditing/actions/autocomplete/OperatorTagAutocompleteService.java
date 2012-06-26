package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;

public class OperatorTagAutocompleteService extends AutocompleteService<OperatorTag> {
	@Autowired
	private OperatorTagDAO dao;

	@Override
	protected Collection<OperatorTag> getItems(String q) {
		return dao.findByOperator(Integer.parseInt(q), true);
	}
}
