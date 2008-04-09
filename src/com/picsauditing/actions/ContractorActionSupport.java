package com.picsauditing.actions;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

public class ContractorActionSupport extends PicsActionSupport {
	protected int id = 0;
	protected ContractorAccount contractor;
	@Autowired
	protected ContractorAccountDAO accountDao;

	protected void findContractor() throws Exception {
		contractor = accountDao.find(id);
		if (contractor.getId() == 0)
			throw new Exception("Contractor " + this.id + " not found");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

}
