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

	protected User u;
	protected String password1;
	protected String password2;
	protected List<EmailSubscription> eList = new ArrayList<EmailSubscription>();
	protected String url;

	private boolean goEmailSub = false;

	/**
	 * This method needs to be anonymous to prevent the user from redirecting on login if the {@link User}'s
	 * forcePasswordReset is true.
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

		// Need to clear the user dao to prevent Hibernate from flushing the changes.
		dao.clear();

		String loginResult = checkProfileEditLogin();
		if (loginResult != null) {
			return loginResult;
		}

		if (dao.duplicateUsername(u.getUsername(), u.getId())) {
			addActionError(getText("ProfileEdit.error.UsernameInUse", u.getUsername()));
			return SUCCESS;
		}

		String result = Strings.validUserName(u.getUsername().trim());
		if (!result.equals("valid")) {
			addActionError(result);
			return SUCCESS;
		}

		u.setPhoneIndex(Strings.stripPhoneNumber(u.getPhone()));
		permissions.setTimeZone(u);
		permissions.setLocale(u.getLocale());

		/*
		 * Some browsers (i.e. Chrome) store the user's password in the first password field. We will assume that if the
		 * confirm password has a value that the user is attempting to change their password.
		 */
		if (!Strings.isEmpty(password2)) {
			boolean forcedReset = u.isForcePasswordReset();
			if (!password1.equals(password2)) {
				addActionError(getText("ProfileEdit.error.PasswordsDoNotMatch"));
			}

			if (!Strings.isEmpty(u.getEmail()) && !Strings.isValidEmail(u.getEmail())) {
				addActionError(getText("ProfileEdit.error.EnterValidEmail"));
			}

			if (getActionErrors().size() > 0) {
				return SUCCESS;
			}

			// Set password to the encrypted version
			u.setEncryptedPassword(password1);

			/*
			 * TODO: this doesn't seem to to anything at the moment.
			 * 
			 * Also, these passwords should not be saved in plain text.
			 */
			int maxHistory = 0;
			u.addPasswordToHistory(password1, maxHistory);

			// If the user is changing their password, they are no longer forced to reset.
			u.setForcePasswordReset(false);
			permissions.setForcePasswordReset(false);

			/*
			 * If the user came to profile edit as a result of a forcedPasswordReset, they will have the `url` field
			 * set.
			 */
			if (!Strings.isEmpty(url) && forcedReset) {
				u = dao.save(u);
				return redirect(url);
			}

		}

		u = dao.save(u);

		/*
		 * This redirct is required if the user happened to change their locale, as we would be stuck in a request for
		 * the previous locale.
		 */
		this.redirect("ProfileEdit.action?success");

		return SUCCESS;
	}

	public String department() {
		return "department";
	}

	/**
	 * This method is used instead of the {@link SecurityInterceptor} method, since the user cannot be redirected on
	 * this page due to the possibility of a `forcePasswordReset`.
	 * 
	 * @return
	 * @throws Exception
	 */
	private String checkProfileEditLogin() throws Exception {

		loadPermissions();

		/*
		 * This should only be null on the `execute` method, since there are no querystring parameters.
		 * 
		 * If the user is set, we have to leave it alone, since the `u` object could be modified.
		 */
		if (u == null) {
			u = dao.find(permissions.getUserId());
		}

		// Only the current user should be allowed to edit their profile.
		if (permissions.getUserId() != u.getId()) {
			throw new NoRightsException(OpPerms.EditProfile, OpType.Edit);
		}

		// If the user is not logged in, they should be redirected to the login page.
		if (!permissions.isLoggedIn()) {
			redirect("Login.action?button=logout&msg=" + getText("ProfileEdit.error.SessionTimeout"));
			return LOGIN;
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

	/**
	 * This method is triggered as a result of a redirect when the user saves his/her profile.
	 * 
	 * @param success
	 *            this parameter is not used.
	 */
	public void setSuccess(boolean success) {
		addActionMessage(getText("ProfileEdit.message.ProfileSavedSuccessfully"));
	}

}
