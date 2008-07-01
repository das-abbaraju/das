package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.mail.EmailAuditBean;
import com.picsauditing.mail.EmailTemplates;

public class OpenAuditsMailer extends PicsActionSupport {

	private ContractorAuditDAO contractorAuditDAO;
	private EmailAuditBean mailer;

	public OpenAuditsMailer(ContractorAuditDAO contractorAuditDAO, EmailAuditBean mailer) {
		this.mailer = mailer;
		this.contractorAuditDAO = contractorAuditDAO;
	}

	public String execute() {
		int nextID = 1;
		while (nextID > 0) {
			nextID = process(nextID);
		}
		return SUCCESS;
	}

	private int process(int nextID) {
		String where = "auditType.hasRequirements = 1 AND auditType.auditTypeID > 1 AND auditStatus = 'Submitted' AND auditID > "
				+ nextID;
		List<ContractorAudit> list = contractorAuditDAO.findWhere(100, where, "auditID");

		nextID = 0;
		for (ContractorAudit conAudit : list) {
			nextID = conAudit.getId();
			try {
				System.out.println("Sending openRequirements email to: (" + conAudit.getId() + ") "
						+ conAudit.getContractorAccount().getName() + " " + conAudit.getAuditType().getAuditName());
				mailer.sendMessage(EmailTemplates.openRequirements, conAudit);
			} catch (Exception e) {
				System.out.println("Error sending openRequirements email: " + e.getMessage());
			}
		}
		return nextID;
	}
}
