package com.picsauditing.mail;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailSubscription;

@SuppressWarnings("serial")
public class EmailSubscriptionSave extends PicsActionSupport {
	protected EmailSubscription eu = null;
	protected EmailSubscriptionDAO emailSubscriptionDAO;

	public EmailSubscriptionSave(EmailSubscriptionDAO emailSubscriptionDAO) {
		this.emailSubscriptionDAO = emailSubscriptionDAO;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;

		eu.setAuditColumns(getUser());
		eu = emailSubscriptionDAO.save(eu);
		return SUCCESS;
	}
	
	public EmailSubscription getEu() {
		return eu;
	}

	public void setEu(EmailSubscription eu) {
		this.eu = eu;
	}
	
	public void prepare() throws Exception {
		int id = this.getParameter("eu.id");
		eu = emailSubscriptionDAO.find(id);
	}

	public SubscriptionTimePeriod[] getSubscriptionTimePeriods() {
		return SubscriptionTimePeriod.values();
	}
}
