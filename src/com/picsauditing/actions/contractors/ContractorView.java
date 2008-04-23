package com.picsauditing.actions.contractors;

import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ContractorView extends ContractorActionSupport {
	
	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
	}
	
	public String execute() throws Exception
	{
		loadPermissions();
		
		findContractor();

		return SUCCESS;
	}

}
