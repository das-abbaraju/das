package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

@SuppressWarnings("serial")
public class ContractorsWidget extends PicsActionSupport {
	ContractorAccountDAO accountDao;

	public ContractorsWidget(ContractorAccountDAO accountDao) {
		this.accountDao = accountDao;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<ContractorAccount> getNewContractors() {
		return accountDao.findNewContractors(permissions, 10);
	}
}
