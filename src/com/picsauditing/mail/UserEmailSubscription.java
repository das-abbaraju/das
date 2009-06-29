package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailSubscription;

@SuppressWarnings("serial")
public class UserEmailSubscription extends PicsActionSupport {
	protected UserDAO userDAO;
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();
	protected int id;
	protected SubscriptionTimePeriod timePeriod;
	protected String subscription;

	public UserEmailSubscription(UserDAO userDAO, EmailSubscriptionDAO emailSubscriptionDAO) {
		this.userDAO = userDAO;
		this.emailSubscriptionDAO = emailSubscriptionDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if("Save".equals(button)) {
				getId();
				getTimePeriod();
				getSubscription();
			}
		}

		List<EmailSubscription> userEmail = emailSubscriptionDAO.findByUserId(permissions.getUserId());
		Map<Subscription, EmailSubscription> eMap = new HashMap<Subscription, EmailSubscription>();
		for(EmailSubscription emailSubscription : userEmail) {
			eMap.put(emailSubscription.getSubscription(), emailSubscription);
		}
		
		for(Subscription subscription : requiredSubscriptionList(permissions)) {
			EmailSubscription eSubscription = eMap.get(subscription);
			if(eSubscription == null) {
				eSubscription = new EmailSubscription();
				eSubscription.setSubscription(subscription);
			}
			eList.add(eSubscription);
		}
		
		return SUCCESS;
	}

	public SubscriptionTimePeriod[] getSubscriptionTimePeriods() {
		return SubscriptionTimePeriod.values();
	}

	public List<Subscription> requiredSubscriptionList(Permissions permissions) {
		List<Subscription> subList = new ArrayList<Subscription>();
		for (Subscription subscription : Subscription.values()) {
			if (permissions.isOperatorCorporate() && subscription.isRequiredForOperator()) {
				subList.add(subscription);
			}
			else if (permissions.isContractor() && subscription.isRequiredForContractor()) {
				subList.add(subscription);
			} else if (subscription.isRequiredForOperator() && subscription.isRequiredForContractor())
				subList.add(subscription);
		}
		return subList;
	}

	public List<EmailSubscription> getEList() {
		return eList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public SubscriptionTimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(SubscriptionTimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public String getSubscription() {
		return subscription;
	}

	public void setSubscription(String subscription) {
		this.subscription = subscription;
	}
}
