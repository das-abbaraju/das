package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.interceptors.SecurityInterceptor;
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
	@Autowired
	protected UserDAO userDAO;

	protected User u;

	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();
	protected String url;

	private boolean goEmailSub = false;
	private boolean usingDynamicReports=false;

	/**
	 * This method needs to be anonymous to prevent the user from redirecting on
	 * login if the {@link User}'s forcePasswordReset is true.
	 */
	@Anonymous
	public String execute() throws Exception {

		String loginResult = checkProfileEditLogin();
		if (loginResult != null) {
			return loginResult;
		}

		return SUCCESS;
	}

	@Anonymous
	public String save() throws Exception {

		// Need to clear the user dao to prevent Hibernate from flushing the
		// changes.
		dao.clear();

		String loginResult = checkProfileEditLogin();
		if (loginResult != null) {
			return loginResult;
		}

		if (dao.duplicateUsername(u.getUsername(), u.getId())) {
			addActionError(getText("ProfileEdit.error.UsernameInUse", u.getUsername()));
			return SUCCESS;
		}

		// TODO: Move this into User-validation.xml and use struts 2 for this
		// validation
		String username = u.getUsername().trim();
		if (Strings.isEmpty(username)) {
			addActionError(getText("User.username.error.Empty"));
			return SUCCESS;
		} else if (username.length() < 3) {
			addActionError(getText("User.username.error.Short"));
			return SUCCESS;
		} else if (username.length() > 100) {
			addActionError(getText("User.username.error.Long"));
			return SUCCESS;
		} else if (username.contains(" ")) {
			addActionError(getText("User.username.error.Space"));
			return SUCCESS;
		} else if (!username.matches("^[a-zA-Z0-9+._@-]{3,50}$")) {
			addActionError(getText("User.username.error.Special"));
			return SUCCESS;
		}

		u.setPhoneIndex(Strings.stripPhoneNumber(u.getPhone()));
		u.setUsingDynamicReports(isUsingDynamicReports());

		permissions.setTimeZone(u);
		permissions.setLocale(u.getLocale());

		u = dao.save(u);

		/*
		 * This redirct is required if the user happened to change their locale,
		 * as we would be stuck in a request for the previous locale.
		 */
		this.redirect("ProfileEdit.action?success");

		return SUCCESS;
	}

	public String department() {
		return "department";
	}

	/**
	 * This method is used instead of the {@link SecurityInterceptor} method,
	 * since the user cannot be redirected on this page due to the possibility
	 * of a `forcePasswordReset`.
	 * 
	 * @return
	 * @throws Exception
	 */
	private String checkProfileEditLogin() throws Exception {

		loadPermissions();

		/*
		 * This should only be null on the `execute` method, since there are no
		 * querystring parameters.
		 * 
		 * If the user is set, we have to leave it alone, since the `u` object
		 * could be modified.
		 */
		if (u == null) {
			u = dao.find(permissions.getUserId());
		}

		// If the user is not logged in, they should be redirected to the login
		// page.
		if (!permissions.isLoggedIn()) {
			addActionMessage(getText("ProfileEdit.error.SessionTimeout"));
			redirect("Login.action?button=logout");

			return LOGIN;
		}

		// Only the current user should be allowed to edit their profile.
		if (permissions.getUserId() != u.getId()) {
			throw new NoRightsException(OpPerms.EditProfile, OpType.Edit);
		}

		// Only users with the edit profile permission can edit their profiles.
		if (!permissions.hasPermission(OpPerms.EditProfile)) {
			addActionError(getText("ProfileEdit.error.MissingEditProfile"));
			return BLANK;
		}

		return null;
	}

	public User getU() {
		return u;
	}

	public void setU(User u) {
		this.u = u;
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
				} else if (permissions.isPicsEmployee() && subscription.isRequiredForAdmin()) {
					subList.add(subscription);
				}
			}
		}
		return subList;
	}

	/**
	 * This is used primarily for redirecting after `forcePasswordReset`.
	 * 
	 * @return
	 */
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

	public boolean isUsingDynamicReports() {
		return usingDynamicReports;
	}

	public void setUsingDynamicReports(boolean usingDynamicReports) {
		this.usingDynamicReports = usingDynamicReports;
	}

	/**
	 * This method is triggered as a result of a redirect when the user saves
	 * his/her profile.
	 * 
	 * @param success
	 *            this parameter is not used.
	 */
	public void setSuccess(boolean success) {
		addActionMessage(getText("ProfileEdit.message.ProfileSavedSuccessfully"));
	}

}
