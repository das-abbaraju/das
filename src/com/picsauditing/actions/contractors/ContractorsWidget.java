package com.picsauditing.actions.contractors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.jpa.entities.BaseTable;

@SuppressWarnings("serial")
public class ContractorsWidget extends PicsActionSupport {
	@Autowired
	protected ContractorAccountDAO accountDao;
	@Autowired
	protected ContractorOperatorDAO contractorOperatorDAO;

	public String execute() throws Exception {
		if (!permissions.isLoggedIn())
			return LOGIN_AJAX;

		return SUCCESS;
	}

	public List<? extends BaseTable> getNewContractors() {
		if (permissions.isAdmin()) {
			return accountDao.findNewContractors(permissions, 20);
		} else if (permissions.isOperatorCorporate()) {
			return contractorOperatorDAO.findNewContractorOperators(permissions.getAccountId(), 10);
		}

		return null;
	}
}
