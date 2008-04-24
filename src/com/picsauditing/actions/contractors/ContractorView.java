package com.picsauditing.actions.contractors;

import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.actions.ContractorActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ContractorView extends ContractorActionSupport {
	private FlagCalculator2 calculator;
	
	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, FlagCalculator2 calculator) {
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
