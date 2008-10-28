package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;

public class EmailQueueList extends PicsActionSupport {
	protected List<EmailQueue> emails = null;
	protected List<EmailQueue> emailsInQueue = new ArrayList<EmailQueue>();
	protected int id;
	protected EmailQueueDAO emailQueueDAO;

	public EmailQueueList(EmailQueueDAO emailQueueDAO) {
		this.emailQueueDAO = emailQueueDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if ("delete".equals(button)) {
			emailQueueDAO.remove(id);
		}

		emails = emailQueueDAO.getPendingEmails("t.createdBy.id = " + permissions.getUserId());
		if (emails.size() > 0)
			emailsInQueue = emailQueueDAO.getPendingEmails("(t.priority > " + emails.get(0).getPriority()
					+ " OR (t.priority = " + emails.get(0).getPriority() + " AND " + "t.id < " + emails.get(0).getId()
					+ "))");
		return SUCCESS;
	}

	public List<EmailQueue> getEmails() {
		return emails;
	}

	public List<EmailQueue> getEmailsInQueue() {
		return emailsInQueue;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
