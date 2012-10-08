package com.picsauditing.actions.autocomplete;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

public class OperatorTagAutocompleteService extends AbstractAutocompleteService<OperatorTag> {

	@Autowired
	private OperatorTagDAO operatorTagDAO;

	@Override
	protected Collection<OperatorTag> getItems(String search, Permissions permissions) {
		if (Strings.isEmpty(search)) {
			return Collections.emptyList();
		}

		List<OperatorTag> tags = operatorTagDAO.findWhere(OperatorTag.class, " t.tag LIKE '%" + Strings.escapeQuotes(search) + "%'");		
		if (CollectionUtils.isEmpty(tags)) {
			return Collections.emptyList();
		}
		
		return tags;
	}

	@Override
	protected Object getKey(OperatorTag operatorTag) {
		return operatorTag.getId();
	}

	@Override
	protected Object getValue(OperatorTag operatorTag, Permissions permissions) {
		return operatorTag.getTag();
	}
}
