package com.picsauditing.actions.contractors;

import com.picsauditing.actions.audits.AuditActionSupport;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

@SuppressWarnings("serial")
public class ConHeader extends AuditActionSupport {
	public ConHeader(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, 
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, CertificateDAO certificateDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao);
	}

	public String execute() throws Exception {
		limitedView = true;
		if (!forceLogin())
			return SUCCESS;

		if (auditID > 0)
			this.findConAudit();
		else
			findContractor();

		return SUCCESS;
	}
}
