package com.picsauditing.actions.contractors;

import com.picsauditing.actions.audits.AuditActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;

public class ConHeader extends AuditActionSupport {
	public ConHeader(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
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
