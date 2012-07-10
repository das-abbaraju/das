package com.picsauditing.actions.autocomplete;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;

@SuppressWarnings("serial")
public class OperatorTagAutocomplete extends AutocompleteActionSupport<OperatorTag> {

	@Autowired
	private OperatorTagDAO dao;

	@Override
	protected Collection<OperatorTag> getItems() {
		return dao.findByOperator(Integer.parseInt(q), true);
	}
}
