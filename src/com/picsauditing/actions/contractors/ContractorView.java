package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.Facilities;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.AccountDAO;

public class ContractorView extends ContractorActionSupport {
	
	public ContractorView(AccountDAO accountDao) {
		this.accountDao = accountDao;
	}
	
	public String execute() throws Exception
	{
		findContractor();

		return SUCCESS;
	}

}
