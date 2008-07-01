package com.picsauditing.mail;

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.Strings;

public class EmailAuditBean extends EmailContractorBean {
	protected ContractorAudit conAudit;

	public EmailAuditBean(ContractorAccountDAO contractorDAO, UserDAO userDAO, AppPropertyDAO appPropertyDAO) {
		super(contractorDAO, userDAO, appPropertyDAO);
	}

	public void sendMessage(EmailTemplates emailType, ContractorAudit conAudit) throws Exception {
		tokens.put("confirmLink", getServerName() + "ScheduleAuditUpdate.action?type=c&auditID=" + conAudit.getId()
				+ "&key="
				+ Strings.hashUrlSafe("c" + conAudit.getContractorAccount().getId() + "id" + conAudit.getId()));
		tokens.put("conAudit", conAudit);
		super.sendMessage(emailType, conAudit.getContractorAccount());
	}
}
