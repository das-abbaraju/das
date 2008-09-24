package com.picsauditing.mail;

import java.util.List;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;

public class EmailQueueList {
	List<EmailQueue> emailQueue = null;
	
	EmailQueueDAO emailQueueDAO;
	
	public EmailQueueList(EmailQueueDAO emailQueueDAO) {
		this.emailQueueDAO = emailQueueDAO;
	}

	public List<EmailQueue> getEmailQueue() {
		return emailQueueDAO.getPendingEmails(100);
	}
}
