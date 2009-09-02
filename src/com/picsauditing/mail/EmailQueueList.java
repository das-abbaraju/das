package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;

@SuppressWarnings("serial")
public class EmailQueueList extends PicsActionSupport {
	protected List<EmailQueue> emails = null;
	protected List<EmailQueue> emailsInQueue = new ArrayList<EmailQueue>();
	protected EmailQueue preview;
	protected int id;
	protected EmailQueueDAO emailQueueDAO;

	public EmailQueueList(EmailQueueDAO emailQueueDAO) {
		this.emailQueueDAO = emailQueueDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		permissions.tryPermission(OpPerms.EmailQueue);
		if ("delete".equals(button)) {
			permissions.tryPermission(OpPerms.EmailQueue, OpType.Delete);
			emailQueueDAO.remove(id);
			return BLANK;
		}
		if ("preview".equals(button)) {
			permissions.tryPermission(OpPerms.EmailQueue);
			preview = emailQueueDAO.find(id);
			return "preview";
		}
		if (permissions.hasPermission(OpPerms.AllContractors) && permissions.hasPermission(OpPerms.AllOperators))
			emails = emailQueueDAO.getPendingEmails("", 50);
		else
			emails = emailQueueDAO.getPendingEmails("t.createdBy.id = " + permissions.getUserId(), 50);
		if (emails.size() > 0)
			emailsInQueue = emailQueueDAO.getPendingEmails("(t.priority > " + emails.get(0).getPriority()
					+ " OR (t.priority = " + emails.get(0).getPriority() + " AND " + "t.id < " + emails.get(0).getId()
					+ "))", 50);
		return SUCCESS;
	}

	public List<EmailQueue> getEmails() {
		return emails;
	}

	public List<EmailQueue> getEmailsInQueue() {
		return emailsInQueue;
	}

	public EmailQueue getPreview() {
		return preview;
	}

	public void setPreview(EmailQueue preview) {
		this.preview = preview;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
