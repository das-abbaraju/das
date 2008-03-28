package com.picsauditing.actions.audits;

import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.OshaLog;

public class VerifyView extends ContractorActionSupport {
	private OshaLog osha;

	public VerifyView(AccountDAO accountDao) {
		this.accountDao = accountDao;
	}

	public String execute() throws Exception {
		contractor = accountDao.find(id);
		if (contractor.getId() == 0)
			return INPUT;

		return SUCCESS;
	}
}
