package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.FlagCalculator;
import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ContractorView extends ContractorActionSupport {
	private FlagCalculator calculator;
	
	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, FlagCalculator calculator) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
		this.calculator = calculator;
	}
	
	public String execute() throws Exception
	{
		loadPermissions();
		
		findContractor();
		
		calculator.runByContractor(this.contractor.getId());

		return SUCCESS;
	}

}
