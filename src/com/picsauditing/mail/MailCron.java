package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.util.log.PicsLogger;

/**
 * Run the email task every minute Send 5 emails at a time This translates into
 * 300/hour or 7200/day If this seems slow, it is because of strict limits by
 * gmail
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class MailCron extends PicsActionSupport {

	private int limit = 5;

	private EmailQueueDAO emailQueueDAO;

	public MailCron(EmailQueueDAO emailQueueDAO) {
		this.emailQueueDAO = emailQueueDAO;
	}

	public String execute() {
		// No authentication required since this runs as a cron job
		PicsLogger.start("EmailSender");
		try {
			List<EmailQueue> emails = emailQueueDAO.getPendingEmails(limit);
			if (emails.size() == 0) {
				addActionMessage("The email queue is empty");
				return SUCCESS;
			}

			// Get the default sender (info@pics)
			EmailSender sender = new EmailSender();
			for (EmailQueue email : emails) {
				try {
					sender.sendNow(email);
				} catch (Exception e) {
					PicsLogger.log("ERROR with MailCron: " + e.getMessage());
					addActionError("Failed to send email: " + e.getMessage());
				}
			}
			if (this.getActionErrors().size() == 0)
				this.addActionMessage("Successfully sent " + emails.size() + " email(s)");

		} finally {
			PicsLogger.stop();
		}
		return SUCCESS;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
