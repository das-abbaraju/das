package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	
	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, 
			AuditBuilder auditBuilder) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
	}
	
	public String execute() throws Exception
	{
		if (!forceLogin())
			return LOGIN;
		findContractor();
		
		auditBuilder.buildAudits(this.contractor);
		
		this.subHeading = "Contractor Details";
		
		return SUCCESS;
	}

}
