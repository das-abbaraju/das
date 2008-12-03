package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;

/**
 * Run the email task every minute
 * Send 5 emails at a time
 * This translates into 300/hour or 7200/day
 * If this seems slow, it is because of strict limits by gmail
 * @author Trevor
 *
 */
@SuppressWarnings("serial")
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
		
		// Get the default sender (info@pics)
		EmailSender sender = new EmailSender();
		for (EmailQueue email : emails) {
			try {
				if (email.getFromPassword() != null && email.getFromPassword().length() > 0) {
					// Use a specific email address like tallred@picsauditing.com
					// We need the password to correctly authenticate with GMail
					EmailSender customSender = new EmailSender(email.getFromAddress(), email.getFromPassword());
					customSender.sendNow(email);
					email.setFromPassword(null);
				} else {
					 // Use the default info@picsauditing.com address
					sender.sendNow(email);
				}
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
