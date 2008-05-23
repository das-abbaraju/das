package com.picsauditing.actions.contractors;

import com.picsauditing.actions.audits.AuditActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

public class ConHeader extends AuditActionSupport {
	public ConHeader(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditDataDAO auditDataDao) {
		super(accountDao, auditDao, auditDataDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return SUCCESS;

		if (auditID > 0)
			this.findConAudit();
		else
			findContractor();

		return SUCCESS;
	}
}
