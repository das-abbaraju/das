package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ContractorRiskRanking extends ContractorActionSupport {

	public ContractorRiskRanking(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
		subHeading = "Services Performed";
	}
	
	public String execute() throws Exception {
		
		return SUCCESS;
	}

}
