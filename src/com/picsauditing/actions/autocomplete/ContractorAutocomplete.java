package com.picsauditing.actions.autocomplete;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.OperatorAccountDAO;

@SuppressWarnings("serial")
public class ContractorAutocomplete extends AutocompleteDynaBean {

	private ContractorAccountDAO dao;

	public ContractorAutocomplete(ContractorAccountDAO dao) {
		this.dao = dao;
	}

	@Override
	protected void findItems() {
		loadPermissions();
		if (!permissions.isAdmin()) {
			// TODO Non admin queries not supported yet
			return;
		}
		items = dao.findWhereNatively(true, "a.name LIKE '%" + Utilities.escapeQuotes(q) + "%'");
	}

	@Override
	protected String createOutput(BasicDynaBean item) {
		return item.get("id") + "|" + item.get("name");
	}
}
