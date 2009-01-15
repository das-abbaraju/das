package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ContractorPaymentOptions extends ContractorActionSupport {
	
	public ContractorPaymentOptions(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}
}
