package com.picsauditing.actions.autocomplete;

import com.picsauditing.access.Permissions;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class FlagCriteriaAutocompleteService extends AbstractAutocompleteService<FlagCriteria> {

	@Autowired
	private FlagCriteriaDAO flagCriteriaDAO;

	@Override
	protected Collection<FlagCriteria> getItemsForSearch(String search, Permissions permissions) {
		String permissionWhere = "";

		String value = "%" + Strings.escapeQuotesAndSlashes(search) + "%";
		List<FlagCriteria> flagCriterias = flagCriteriaDAO.findByTranslatableField(FlagCriteria.class, permissionWhere, "label",
				value, permissions.getLocale(), RESULT_SET_LIMIT);
		return flagCriterias;
	}

	@Override
	protected Object getKey(FlagCriteria flagCriteria) {
		return flagCriteria.getId();
	}

	@Override
	protected Object getValue(FlagCriteria flagCriteria, Permissions permissions) {
		return flagCriteria.getLabel(); // .toString(permissions.getLocale());
	}

	@Override
	protected Collection<FlagCriteria> getItemsForSearchKey(String searchKey, Permissions permissions) {
		int flagCriteriaID = NumberUtils.toInt(searchKey);
		if (flagCriteriaID == 0) {
			return Collections.emptyList();
		}

		return Arrays.asList(flagCriteriaDAO.find(flagCriteriaID));
	}

}
