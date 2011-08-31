package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.UserSwitch;
import com.picsauditing.mail.Subscription;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ProfileEdit extends PicsActionSupport {
	@Autowired
	protected UserDAO dao;
	@Autowired
	protected ContractorAccountDAO accountDao;
	@Autowired
	protected UserSwitchDAO userSwitchDao;
	@Autowired
	protected EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired
	protected UserLoginLogDAO loginLogDao;

	protected User u;
	protected String password1;
	protected String password2;
	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();
	protected String url;

	private boolean goEmailSub = false;

	@Anonymous
	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.isLoggedIn()) {
			redirect("Login.action?button=logout&msg=" + getText("ProfileEdit.error.SessionTimeout"));
			return LOGIN;
		}

		if (!permissions.hasPermission(OpPerms.EditProfile)) {
			addActionError(getText("ProfileEdit.error.MissingEditProfile"));
			return BLANK;
		}

		u = dao.find(permissions.getUserId());

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.EditProfile, type = OpType.Edit)
	public String save() throws Exception {
		dao.clear();

		if (dao.duplicateUsername(u.getUsername(), u.getId())) {
			addActionError(getText("ProfileEdit.error.UsernameInUse", u.getUsername()));
			return SUCCESS;
		}

		String result = Strings.validUserName(u.getUsername().trim());
		if (!result.equals("valid")) {
			addActionError(result);
			return SUCCESS;
		}

		if (!Strings.isEmpty(password2)) {
			if (!password1.equals(password2))
				addActionError(getText("ProfileEdit.error.PasswordsDoNotMatch"));

			if (!Strings.isEmpty(u.getEmail()) && !Strings.isValidEmail(u.getEmail()))
				addActionError(getText("ProfileEdit.error.EnterValidEmail"));

			if (getActionErrors().size() > 0)
				return SUCCESS;
			int maxHistory = 0;
			// u.getAccount().getPasswordPreferences().getMaxHistory()
			// TODO: Check is addPasswordToHistory is still needed
			u.addPasswordToHistory(password1, maxHistory);
			u.setEncryptedPassword(password1);
			if (!Strings.isEmpty(url) && u.isForcePasswordReset())
				redirect(url);

			u.setForcePasswordReset(false);
			permissions.setForcePasswordReset(false);
		}
		u.setPhoneIndex(Strings.stripPhoneNumber(u.getPhone()));
		permissions.setTimeZone(u);
		permissions.setLocale(u.getLocale());
		ActionContext.getContext().getSession().put("permissions", permissions);
		u = dao.save(u);
		permissions.setLocale(u.getLocale());

		addActionMessage(getText("ProfileEdit.message.ProfileSavedSuccessfully"));

		return SUCCESS;
	}
	
	public String department() {
		return "department";
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

	public List<UserLoginLog> getRecentLogins() {
		return loginLogDao.findRecentLogins(u.getId(), 10);
	}

	public List<EmailSubscription> getEList() {
		if (eList.size() == 0) {
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
		}
		return eList;
	}

	public List<Subscription> requiredSubscriptionList(Permissions permissions) {
		List<Subscription> subList = new ArrayList<Subscription>();
		for (Subscription subscription : Subscription.values()) {
			if (subscription.getRequiredPerms() == null || permissions.hasPermission(subscription.getRequiredPerms())) {
				if (permissions.isOperatorCorporate() && subscription.isRequiredForOperator()) {
					subList.add(subscription);
				} else if (permissions.isContractor() && subscription.isRequiredForContractor()) {
					subList.add(subscription);
				} else if (subscription.isRequiredForOperator() && subscription.isRequiredForContractor()) {
					subList.add(subscription);
				} else if (permissions.isRequiresOQ() && subscription.isRequiresOQ()) {
					subList.add(subscription);
				}
			}
		}
		return subList;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setGoEmailSub(boolean goEmailSub) {
		this.goEmailSub = goEmailSub;
	}

	public boolean isGoEmailSub() {
		return goEmailSub;
	}

}
