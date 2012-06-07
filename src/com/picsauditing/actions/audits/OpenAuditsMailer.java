package com.picsauditing.actions.audits;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class OpenAuditsMailer extends PicsActionSupport {

	private ContractorAuditDAO contractorAuditDAO;
	private EmailQueueDAO emailQueueDAO;
    
	private final Logger LOG = LoggerFactory.getLogger(OpenAuditsMailer.class);
	
	public OpenAuditsMailer(ContractorAuditDAO contractorAuditDAO, EmailQueueDAO emailQueueDAO) {
		this.contractorAuditDAO = contractorAuditDAO;
		this.emailQueueDAO = emailQueueDAO;
	}

	@Anonymous
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
				+ "AND id IN (SELECT audit.id FROM ContractorAuditOperator WHERE status = 'Submitted') AND id > "
				+ nextID;
		List<ContractorAudit> list = contractorAuditDAO.findWhere(100, where, "id");
		if (list.size() == 0)
			return 0;

		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		EmailBuilder emailBuilder = new EmailBuilder();

		nextID = 0;
		for (ContractorAudit conAudit : list) {
			nextID = conAudit.getId();
			try {
				emailBuilder.clear();
				emailBuilder.setTemplate(6);
				emailBuilder.setPermissions(permissions);
				emailBuilder.setConAudit(conAudit);
				EmailQueue email = emailBuilder.build();
				email.setVeryLowPriority();
				email.setFromAddress("audits@picsauditing.com");
				email.setViewableById(Account.EVERYONE);
				emailQueueDAO.save(email);

				Note note = new Note();
				note.setAccount(conAudit.getContractorAccount());
				note.setAuditColumns(permissions);
				note.setSummary("Sent Open Requirements Reminder email to " + emailBuilder.getSentTo());
				note.setNoteCategory(NoteCategory.Audits);
				note.setViewableById(Account.EVERYONE);
				noteDAO.save(note);

			} catch (Exception e) {
				LOG.error("Error sending openRequirements email: {}", e.getMessage());
			}
		}
		return nextID;
	}
}
