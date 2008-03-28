package com.picsauditing.actions;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.Account;

public class ContractorActionSupport extends PicsActionSupport
{
	protected int id = 0;
	protected Account contractor;
	protected AccountDAO accountDao;
	
	protected void findContractor() throws Exception {
		contractor = accountDao.find(id);
		if (contractor.getId() == 0)
			throw new Exception("Contractor "+this.id+" not found");
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
