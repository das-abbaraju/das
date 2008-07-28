package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ContractorThumbnail extends ContractorActionSupport {
	ContractorAccountDAO accountDao;
	
	public ContractorThumbnail(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		findContractor();
		
		return SUCCESS;
	}

}
