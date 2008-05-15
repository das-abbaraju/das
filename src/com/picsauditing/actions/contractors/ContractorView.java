package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ContractorView extends ContractorActionSupport {
	private FlagCalculator2 calculator;
	
	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, FlagCalculator2 calculator) {
		super(accountDao, auditDao);
		this.calculator = calculator;
	}
	
	public String execute() throws Exception
	{
		if (!forceLogin())
			return LOGIN;
		findContractor();
		
		this.subHeading = "Contractor Details";
		
		return SUCCESS;
	}

}
