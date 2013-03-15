package com.picsauditing.actions.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.interceptors.SecurityInterceptor;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.UserSwitch;
import com.picsauditing.mail.Subscription;
import com.picsauditing.security.EncodedKey;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.InputValidator;

@SuppressWarnings("serial")
public class ProfileEdit extends PicsActionSupport {

	@Autowired
	private UserDAO dao;
	@Autowired
	private UserSwitchDAO userSwitchDao;
	@Autowired
	private EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired
	private UserLoginLogDAO loginLogDao;
	@Autowired
	private InputValidator inputValidator;
	@Autowired
	private FeatureToggle featureToggle;

	private User u;

	private List<EmailSubscription> eList = new ArrayList<EmailSubscription>();

	private boolean goEmailSub = false;
	private boolean usingVersion7Menus = false;

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
		// Need to clear the user dao to prevent Hibernate from flushing the changes.
		dao.clear();

		String loginResult = checkProfileEditLogin();
		if (loginResult != null) {
			return loginResult;
		}

		validateInput();
		if (hasFieldErrors()) {
			return INPUT_ERROR;
		}

		u.setPhoneIndex(Strings.stripPhoneNumber(u.getPhone()));
		u.setUsingVersion7Menus(isUsingVersion7Menus());
		if (!featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
			u.setUsingDynamicReports(isUsingVersion7Menus());
		}

		permissions.setTimeZone(u);
		permissions.setLocale(u.getLocale());

		u = dao.save(u);
		dao.refresh(u);

		// We have to redirect to refresh the locale, if it has been changed
		return redirect();
	}

	private String redirect() throws IOException {
		if (isUserSetForNewMenu()) {
			// if the user makes it to this point, we know their user information was saved properly.
			permissions.setUsingVersion7Menus(true);
			ActionContext.getContext().getSession().put("permissions", permissions);
			return setUrlForRedirect("Tutorial!navigationMenu.action");
		}

		addActionMessage(getText("ProfileEdit.message.ProfileSavedSuccessfully"));

		return setUrlForRedirect("ProfileEdit.action");
	}

	private boolean isUserSetForNewMenu() {
		if(featureToggle.isFeatureEnabled(FeatureToggle.TOGGLE_USE_V7_MENU_COLUMN)) {
			return isUsingVersion7Menus() && u.getUsingVersion7MenusDate() == null;
		}

		return isUsingVersion7Menus() && u.getUsingDynamicReportsDate() == null;
	}

	public void validateInput() {
		String errorMessageKey = inputValidator.validateName(u.getName());
		addFieldErrorIfMessage("u.name", errorMessageKey);

		errorMessageKey = inputValidator.validateName(u.getDepartment(), false);
		addFieldErrorIfMessage("u.department", errorMessageKey);

		errorMessageKey = inputValidator.validateEmail(u.getEmail());
		addFieldErrorIfMessage("u.email", errorMessageKey);

		errorMessageKey = inputValidator.validateUsername(u.getUsername());
		addFieldErrorIfMessage("u.username", errorMessageKey);

		errorMessageKey = inputValidator.validateUsernameAvailable(u.getUsername(), permissions.getUserId());
		addFieldErrorIfMessage("u.username", errorMessageKey);

		errorMessageKey = inputValidator.validatePhoneNumber(u.getPhone(), false);
		addFieldErrorIfMessage("u.phone", errorMessageKey);

		errorMessageKey = inputValidator.validatePhoneNumber(u.getFax(), false);
		addFieldErrorIfMessage("u.fax", errorMessageKey);

		errorMessageKey = inputValidator.validateLocale(u.getLocale());
		addFieldErrorIfMessage("u.locale", errorMessageKey);

		if (u.getTimezone() == null) {
			addFieldErrorIfMessage("u.timezone", InputValidator.REQUIRED_KEY);
		}
	}

	@SuppressWarnings("unchecked")
	public String generateApiKey() {
		String apiKey = EncodedKey.randomApiKey();
		User u = getUser();
		u.setApiKey(apiKey);
		json.put("ApiKey", apiKey);
		json.put("ApiCheck", getRequestHost() + "/ApiCheck.action?valueToEcho=2&apiKey=" + apiKey);
		userDAO.save(u);
		return JSON;
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

		// If the user is not logged in, they should be redirected to the login page.
		if (!permissions.isLoggedIn()) {
			addActionMessage(getText("ProfileEdit.error.SessionTimeout"));
			return setUrlForRedirect("Login.action?button=logout");
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

		// Update invalid locale to valid one
		if (!supportedLanguages.getStableAndBetaLanguageLocales().contains(u.getLocale())) {
			u.setLocale(supportedLanguages.getNearestStableAndBetaLocale(u.getLocale()));
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

	public List<AuditType> getViewableAuditsList() {
		AuditTypeDAO auditTypeDao = SpringUtils.getBean(SpringUtils.AUDIT_TYPE_DAO);

		return auditTypeDao.findWhere("t.id IN (" + Strings.implode(permissions.getVisibleAuditTypes()) + ")");
	}

	public List<User> getAllInheritedGroups() {
		List<User> users = userDAO.findByIDs(User.class, permissions.getAllInheritedGroupIds());
		Collections.sort(users, new Comparator<User>() {

			@Override
			public int compare(User o1, User o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}

		});

		return users;
	}

	public void setGoEmailSub(boolean goEmailSub) {
		this.goEmailSub = goEmailSub;
	}

	public boolean isGoEmailSub() {
		return goEmailSub;
	}

	public boolean isUsingVersion7Menus() {
		return usingVersion7Menus;
	}

	public void setUsingVersion7Menus(boolean usingVersion7Menus) {
		this.usingVersion7Menus = usingVersion7Menus;
	}

}
