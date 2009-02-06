package com.picsauditing.actions.contractors;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class InvoiceDetail extends ContractorActionSupport {
	public InvoiceDetail(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao) {
		super(accountDao, auditDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findContractor();

		this.subHeading = "Invoice Detail";
		return SUCCESS;
	}
}
