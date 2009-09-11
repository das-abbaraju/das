package com.picsauditing.mail;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class EmailSubscriptionSave extends PicsActionSupport {
	protected EmailSubscription eu = null;
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	protected boolean addsubscription = false;
	protected SubscriptionTimePeriod sPeriod = null;

	public EmailSubscriptionSave(EmailSubscriptionDAO emailSubscriptionDAO) {
		this.emailSubscriptionDAO = emailSubscriptionDAO;
	}

	public String execute() {
		if (!forceLogin())
			return LOGIN;
		if (eu.getId() == 0)
			eu.setUser(new User(permissions.getUserId()));

		eu.setAuditColumns(permissions);
		if (addsubscription) {
			if (sPeriod == null) {
				SubscriptionTimePeriod[] sPeriods = eu.getSubscription().getSupportedTimePeriods();
				if (sPeriods.length == 2)
					eu.setTimePeriod(sPeriods[1]);
				else
					eu.setTimePeriod(SubscriptionTimePeriod.Weekly);
			} else
				eu.setTimePeriod(sPeriod);
		} else
			eu.setTimePeriod(SubscriptionTimePeriod.None);
		eu = emailSubscriptionDAO.save(eu);
		return SUCCESS;
	}

	public SubscriptionTimePeriod getSPeriod() {
		return sPeriod;
	}

	public void setSPeriod(SubscriptionTimePeriod period) {
		sPeriod = period;
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

	public boolean isAddsubscription() {
		return addsubscription;
	}

	public void setAddsubscription(boolean addsubscription) {
		this.addsubscription = addsubscription;
	}
}
