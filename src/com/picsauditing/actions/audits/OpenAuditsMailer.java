package com.picsauditing.actions.audits;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.SpringUtils;

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
		// Only send this to Desktop, Office and D&A
		String where = "contractorAccount.status = 'Active' AND auditType.id IN (2,3,6) "
				+ "AND auditStatus = 'Submitted' AND id > " + nextID;
		List<ContractorAudit> list = contractorAuditDAO.findWhere(100, where, "id");
		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		EmailBuilder emailBuilder = new EmailBuilder();

		nextID = 0;
		for (ContractorAudit conAudit : list) {
			boolean requiresAudit = false;
			for (ContractorOperator contractorOperator : conAudit.getContractorAccount().getOperators()) {
				if (contractorOperator.getOperatorAccount().getStatus().isActiveDemo()) {
					for (AuditOperator auditOperator : contractorOperator.getOperatorAccount().getVisibleAudits()) {
						if (conAudit.getAuditType() == auditOperator.getAuditType()
								&& auditOperator.isRequiredFor(conAudit.getContractorAccount())) {
							requiresAudit = true;
							break;
						}
					}
					if (requiresAudit)
						break;
				}
			}

			if (requiresAudit) {
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

					Note note = new Note();
					note.setAccount(conAudit.getContractorAccount());
					note.setAuditColumns(permissions);
					note.setSummary("Sent Open Requirements Reminder email to " + emailBuilder.getSentTo());
					note.setNoteCategory(NoteCategory.Audits);
					note.setViewableById(Account.EVERYONE);
					noteDAO.save(note);

				} catch (Exception e) {
					System.out.println("Error sending openRequirements email: " + e.getMessage());
				}
			}
		}
		return nextID;
	}
}
