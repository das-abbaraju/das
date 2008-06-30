package com.picsauditing.actions.report;

import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.mail.EmailAuditBean;
import com.picsauditing.mail.EmailUserBean;

public class AuditAssignmentUpdateAuditorOnly extends AuditAssignmentUpdate {

	public AuditAssignmentUpdateAuditorOnly(ContractorAuditDAO dao, UserDAO userDao,
			EmailUserBean auditorMailer, EmailAuditBean contractorMailer) {
		super( dao, userDao, auditorMailer, contractorMailer );
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		auditor = userDao.find(auditor.getId());
		contractorAudit.setAuditor(auditor);

		dao.save(contractorAudit);
		
		return SUCCESS;
	}
}