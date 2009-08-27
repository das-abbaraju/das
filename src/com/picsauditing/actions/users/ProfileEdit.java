package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.UserSwitch;
import com.picsauditing.mail.Subscription;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ProfileEdit extends PicsActionSupport implements Preparable {
	protected User u;
	protected UserDAO dao;
	protected ContractorAccountDAO accountDao;
	protected UserSwitchDAO userSwitchDao;
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	protected String password1;
	protected String password2;
	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();

	public ProfileEdit(UserDAO dao, ContractorAccountDAO accountDao, UserSwitchDAO userSwitchDao,
			EmailSubscriptionDAO emailSubscriptionDAO) {
		this.dao = dao;
		this.accountDao = accountDao;
		this.userSwitchDao = userSwitchDao;
		this.emailSubscriptionDAO = emailSubscriptionDAO;
	}
	
	public void prepare() throws Exception {
		getPermissions();
		u = dao.find(permissions.getUserId());
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.EditProfile);

		//u = dao.find(permissions.getUserId());

		if (dao.duplicateUsername(u.getUsername(), u.getId())) {
			addActionError("Another user is already using the username: " + u.getUsername());
			return SUCCESS;
		}
		if (button != null) {
			if (!Strings.isEmpty(password1)) {
				if (!password1.equals(password2) && !password1.equals(u.getPassword()))
					addActionError("Passwords don't match");

				if (!Strings.isEmpty(u.getEmail()) && !Utilities.isValidEmail(u.getEmail()))
					addActionError("Please enter a valid email address. This is our main way of communicating with you so it must be valid.");

				if (getActionErrors().size() > 0)
					return SUCCESS;
				int maxHistory = 0;
				// u.getAccount().getPasswordPreferences().getMaxHistory()
				u.addPasswordToHistory(password1, maxHistory);
				u.setPassword(password1);
			}
			u.setPhoneIndex(Strings.stripPhoneNumber(u.getPhone()));
			u = dao.save(u);

			if (u.getAccount().isContractor()) {
				ContractorAccount contractor = (ContractorAccount) u.getAccount();
				contractor.setContact(u.getName());
				contractor.setEmail(u.getEmail());
				contractor.setPhone(u.getPhone());
				accountDao.save(contractor);
			}

			addActionMessage("Your profile was saved successfully");
		}

		return SUCCESS;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public List<UserSwitch> getSwitchTos() {
		return userSwitchDao.findByUserId(u.getId());
	}

	public List<AuditType> getViewableAuditsList() {
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		String auditsList = Strings.implode(permissions.getCanSeeAudit(), ",");
		if (auditsList.length() > 0)
			return dao.findWhere("id IN (" + auditsList + ")");
		return new ArrayList<AuditType>();
	}

	public List<UserLoginLog> getRecentLogins() {
		UserLoginLogDAO loginLogDao = (UserLoginLogDAO) SpringUtils.getBean("UserLoginLogDAO");
		return loginLogDao.findRecentLogins(u.getUsername(), 10);
	}

	public List<EmailSubscription> getEList() {
		List<EmailSubscription> userEmail = emailSubscriptionDAO.findByUserId(permissions.getUserId());
		Map<Subscription, EmailSubscription> eMap = new HashMap<Subscription, EmailSubscription>();
		for (EmailSubscription emailSubscription : userEmail) {
			eMap.put(emailSubscription.getSubscription(), emailSubscription);
		}

		for (Subscription subscription : requiredSubscriptionList(permissions)) {
			EmailSubscription eSubscription = eMap.get(subscription);
			if (eSubscription == null) {
				eSubscription = new EmailSubscription();
				eSubscription.setSubscription(subscription);
			}
			eList.add(eSubscription);
		}
		return eList;
	}

	public List<Subscription> requiredSubscriptionList(Permissions permissions) {
		List<Subscription> subList = new ArrayList<Subscription>();
		for (Subscription subscription : Subscription.values()) {
			if (permissions.isOperatorCorporate() && subscription.isRequiredForOperator()) {
				subList.add(subscription);
			} else if (permissions.isContractor() && subscription.isRequiredForContractor()) {
				subList.add(subscription);
			} else if (subscription.isRequiredForOperator() && subscription.isRequiredForContractor())
				subList.add(subscription);
		}
		return subList;
	}
}
