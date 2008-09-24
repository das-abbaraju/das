package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;

public class EmailQueueList extends PicsActionSupport {
	protected List<EmailQueue> emails = null;
	
	protected EmailQueueDAO emailQueueDAO;
	private EmailStatus status;
	
	
	public EmailQueueList(EmailQueueDAO emailQueueDAO) {
		this.emailQueueDAO = emailQueueDAO;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		status = EmailStatus.Pending;
		emails = emailQueueDAO.getPendingEmails(100);
		
		return SUCCESS;
	}

	public List<EmailQueue> getEmails() {
		return emails;
	}

	public EmailStatus getStatus() {
		return status;
	}

	public void setStatus(EmailStatus status) {
		this.status = status;
	}

	public EmailStatus[] getEmailStatuses() {
		return EmailStatus.values();
	}
}
