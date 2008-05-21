package com.picsauditing.actions.contractors;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ContractorView extends ContractorActionSupport {
	private FlagCalculator2 calculator;
	private AuditBuilder auditBuilder;
	
	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, 
			FlagCalculator2 calculator, AuditBuilder auditBuilder) {
		super(accountDao, auditDao);
		this.calculator = calculator;
		this.auditBuilder = auditBuilder;
	}
	
	public String execute() throws Exception
	{
		if (!forceLogin())
			return LOGIN;
		findContractor();
		
		Calendar cal = new GregorianCalendar(); 
		
		long start = cal.getTimeInMillis();
		auditBuilder.buildAudits(this.id);
		long end = cal.getTimeInMillis();
		System.out.println("Elapsed time = "+ (end - start) + " milliseconds");
		
		this.subHeading = "Contractor Details";
		
		return SUCCESS;
	}

}
