package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;

/**
 * Run the email task every minute
 * Send 5 emails at a time
 * This translates into 300/hour or 7200/day
 * If this seems slow, it is because of strict limits by gmail
 * @author Trevor
 *
 */
public class MailCron extends PicsActionSupport {

	EmailQueueDAO emailQueueDAO;
	
	public MailCron(EmailQueueDAO emailQueueDAO) {
		this.emailQueueDAO = emailQueueDAO;
	}
	
	public String execute() {
		// No authentication required since this runs as a cron job
		
		List<EmailQueue> emails = emailQueueDAO.getPendingEmails(5);
		if (emails.size() == 0) {
			addActionMessage("The email queue is empty");
			return SUCCESS;
		}
		for (EmailQueue email : emails) {
			try {
				EmailSender.send(email);
				email.setStatus(EmailStatus.Sent);
				emailQueueDAO.save(email);
			} catch (Exception e) {
				System.out.println("ERROR with MailCron: " + e.getMessage());
				addActionError("Failed to send email: " + e.getMessage());
			}
		}
		if (this.getActionErrors().size() == 0)
			this.addActionMessage("Successfully sent " + emails.size() + " email(s)");
		return SUCCESS;
	}
}
