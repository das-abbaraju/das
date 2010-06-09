package com.picsauditing.actions.contractors;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.PicsDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;

@SuppressWarnings("serial")
public class ContractorsWidget extends PicsActionSupport {
	ContractorAccountDAO accountDao;
	ContractorOperatorDAO contractorOperatorDAO;

	public ContractorsWidget(ContractorAccountDAO accountDao,
			ContractorOperatorDAO contractorOperatorDAO) {
		this.accountDao = accountDao;
		this.contractorOperatorDAO = contractorOperatorDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<? extends BaseTable> getNewContractors() {
		if(permissions.isAdmin()){
			return accountDao.findNewContractors(permissions, 10);
		} else if(permissions.isOperatorCorporate()){
			return contractorOperatorDAO.findNewContractorOperators(permissions.getAccountId(), 10);
		}
		return null;
	}
}
