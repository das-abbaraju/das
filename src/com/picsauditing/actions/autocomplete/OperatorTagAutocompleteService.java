package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

public class OperatorTagAutocompleteService extends AbstractAutocompleteService<OperatorTag> {

	@Autowired
	private OperatorAccountDAO operatorDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;

	@Override
	protected Collection<OperatorTag> getItems(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		List<OperatorAccount> operators = operatorDAO.findWhere(OperatorAccount.class,
				" t.name LIKE '%" + Strings.escapeQuotes(search) + "%'");		
		if (CollectionUtils.isEmpty(operators)) {
			return Collections.emptyList();
		}
		
		// TODO: This is just temporary to get this all working for DR, so we will just return the first Operator
		return operatorTagDAO.findByOperator(operators.get(0).getId(), true);
	}

	@Override
	protected Object getAutocompleteItem(OperatorTag operatorTag) {
		return operatorTag.getAutocompleteItem();
	}

	@Override
	protected Object getAutocompleteValue(OperatorTag operatorTag) {
		return operatorTag.getAutocompleteValue();
	}
}
