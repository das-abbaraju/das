package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;

public class MailCron extends PicsActionSupport {

	EmailQueueDAO emailQueueDAO;
	
	public MailCron(EmailQueueDAO emailQueueDAO) {
		this.emailQueueDAO = emailQueueDAO;
	}
	
	public String execute() {
		// No authentication required since this runs as a cron job
		List<EmailQueue> emails = emailQueueDAO.getPendingEmails(10);
		for (EmailQueue email : emails) {
			try {
				// TODO send email here
				email.setStatus(EmailStatus.Sent);
				emailQueueDAO.save(email);
			} catch (Exception e) {
				System.out.println("ERROR with MailCron: " + e.getMessage());
			}
		}
		return SUCCESS;
	}
}
