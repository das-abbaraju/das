package com.picsauditing.actions.autocomplete;

import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;

@SuppressWarnings("serial")
public class OperatorTagAutocomplete extends AutocompleteActionSupport<OperatorTag> {

	private OperatorTagDAO dao;

	public OperatorTagAutocomplete(OperatorTagDAO dao) {
		this.dao = dao;
	}

	@Override
	protected void findItems() {
		items = dao.findByOperator(Integer.parseInt(q), true);
	}
}
