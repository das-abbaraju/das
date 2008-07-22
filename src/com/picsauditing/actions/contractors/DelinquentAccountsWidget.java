package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

public class DelinquentAccountsWidget extends PicsActionSupport {
	ContractorAccountDAO accountDao;

	public DelinquentAccountsWidget(ContractorAccountDAO accountDao) {
		this.accountDao = accountDao;
	}

	public String execute() throws Exception {
		loadPermissions();
		return SUCCESS;
	}

	public List<ContractorAccount> getDelinquentContractors() {
		return accountDao.findDelinquentContractors(permissions, 10);
	}
}
