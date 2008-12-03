package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.mail.EmailBuilder;

@SuppressWarnings("serial")
public class OpenAuditsMailer extends PicsActionSupport {

	private ContractorAuditDAO contractorAuditDAO;
	private EmailQueueDAO emailQueueDAO;

	public OpenAuditsMailer(ContractorAuditDAO contractorAuditDAO, EmailQueueDAO emailQueueDAO) {
		this.contractorAuditDAO = contractorAuditDAO;
		this.emailQueueDAO = emailQueueDAO;
	}

	public String execute() {
		int nextID = 1;
		while (nextID > 0) {
			nextID = process(nextID);
		}
		return SUCCESS;
	}

	private int process(int nextID) {
		String where = "contractorAccount.active = 'Y' AND auditType.hasRequirements = 1 AND auditType.auditTypeID > 1 AND auditStatus = 'Submitted' AND auditID > "
				+ nextID;
		List<ContractorAudit> list = contractorAuditDAO.findWhere(100, where, "auditID");

		EmailBuilder emailBuilder = new EmailBuilder();

		nextID = 0;
		for (ContractorAudit conAudit : list) {
			if (!conAudit.getContractorAccount().getRiskLevel().equals(LowMedHigh.Low)) {
				nextID = conAudit.getId();
				try {
					System.out.println("Sending openRequirements email to: (" + conAudit.getId() + ") "
							+ conAudit.getContractorAccount().getName() + " " + conAudit.getAuditType().getAuditName());
					emailBuilder.clear();
					emailBuilder.setTemplate(6);
					emailBuilder.setPermissions(permissions);
					emailBuilder.setConAudit(conAudit);
					EmailQueue email = emailBuilder.build();
					email.setPriority(10);
					email.setFromAddress("audits@picsauditing.com");
					emailQueueDAO.save(email);
					ContractorBean.addNote(conAudit.getContractorAccount().getId(), permissions,
							"Sent Open Requirements Reminder email to " + emailBuilder.getSentTo());
				} catch (Exception e) {
					System.out.println("Error sending openRequirements email: " + e.getMessage());
				}
			}
		}
		return nextID;
	}
}
