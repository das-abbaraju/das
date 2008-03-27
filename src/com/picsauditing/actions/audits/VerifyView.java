package com.picsauditing.actions.audits;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.OshaLog;

public class VerifyView extends PicsActionSupport {
	private int id = 0;
	private Account contractor;
	private AccountDAO accountDao;
	private OshaLog osha;
	
	public VerifyView(AccountDAO accountDao) {
		this.accountDao = accountDao;
	}
	public String execute() throws Exception {
		contractor = accountDao.find(id);
		if (contractor.getId() == 0) return INPUT;
		
		
		return SUCCESS;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Account getContractor() {
		return contractor;
	}
	public void setContractor(Account contractor) {
		this.contractor = contractor;
	}
	
	
}
