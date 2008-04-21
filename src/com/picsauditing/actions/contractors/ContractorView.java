package com.picsauditing.actions.contractors;

import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;

public class ContractorView extends ContractorActionSupport {
	
	public ContractorView(ContractorAccountDAO accountDao) {
		this.accountDao = accountDao;
	}
	
	public String execute() throws Exception
	{
		getPermissions();
		
		findContractor();

		return SUCCESS;
	}

}
