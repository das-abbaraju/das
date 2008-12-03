package com.picsauditing.actions.audits;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;

@SuppressWarnings("serial")
public class VerifySaveFollowUp extends PicsActionSupport {
	public VerifySaveFollowUp(AccountDAO accountDao) {
		//this.accountDao = accountDao;
	}

	public String execute() throws Exception {
		return SUCCESS;
	}
}
