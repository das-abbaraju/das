package com.picsauditing.actions.contractors;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;


@SuppressWarnings("serial")
public class BillingDetail extends ContractorActionSupport {
	private AccountDAO accountDAO;
	
	public BillingDetail(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
		this.accountDAO = accountDAO;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();

		return SUCCESS;
	}	
}
