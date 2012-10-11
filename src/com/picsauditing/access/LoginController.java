package com.picsauditing.access;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.LocaleController;
import com.picsauditing.util.Strings;

/**
 * Populate the permissions object in session with appropriate login credentials
 * and access/permission data
 */
@SuppressWarnings("serial")
public class LoginController extends PicsActionSupport {
	private static final int ONE_SECOND = 1;

	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected UserLoginLogDAO loginLogDAO;

	// used to inject mock permissions for testing
	private Permissions permissionsForTest;
	private User user;
	private String email;
	private String username;
	private String password;
	private String key;
	private int switchToUser;
	private boolean rememberMe = false;

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final int MAX_FAILED_ATTEMPTS = 6;

	@Anonymous
	@Override
	public String execute() throws Exception {
		if (button == null) {
			if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_SESSION_COOKIE)) {
				if (sessionCookieIsValidAndNotExpired()) {
					switchToUser = getClientSessionUserID();
					return switchTo();
				}
			}
			return SUCCESS;
		} else if ("confirm".equals(button)) {
			return confirm();
		} else if ("logout".equals(button)) {
			return logout();
		} else if ("switchBack".equalsIgnoreCase(button)) {
			return switchBack();
		} else { // normal login
			if (switchToUser > 0) {
				return switchTo();
			}
			return login();
		}
	}

	/**
	 * Method to log in via an ajax overlay
	 */
	@SuppressWarnings("unchecked")
	@Anonymous
	public String ajax() throws Exception {
		if (!AjaxUtils.isAjax(getRequest()))
			return BLANK;

		execute();

		json = new JSONObject();
		json.put("loggedIn", permissions.isLoggedIn());
		return JSON;
	}

	/**
	 * Result for when the user is not logged in during an ajax request.
	 */
	@Anonymous
	public String overlay() {
		setRedirect(true);
		return "overlay";
	}

	/**
	 * Result when user changes language on login page.
	 */
	@Anonymous
	// TODO Change login modal to this form as well
	public String loginform() {
		return "loginform";
	}

	public String confirm() {
		try {
			user = userDAO.findName(username);
			user.setEmailConfirmedDate(new Date());
			userDAO.save(user);
			addActionMessage(getText("Login.ConfirmedEmailAddress"));
		} catch (Exception e) {
			addActionError(getText("Login.AccountConfirmationFailed"));
		}
		return SUCCESS;
	}

	public String logout() throws Exception {
		loadPermissions(false); // FIXME: (maybe) why are we loading permissions
								// only to clear them and invalidate a session?
		permissions.clear();
		invalidateSession();
		clearPicsOrgCookie();
		return SUCCESS;
	}

	private String switchBack() throws Exception {
		loadPermissions(false);
		switchToUser = 0;
		int originalUser = getClientSessionOriginalUserID();
		if (originalUser > 0) {
			user = userDAO.find(originalUser);
			permissions.login(user);
			LocaleController.setLocaleOfNearestSupported(permissions);
			addClientSessionCookieToResponse(rememberMe, 0);
			permissions.setAdminID(0);
		}
		return setRedirectUrlPostLogin();
	}

	// private void determineSwitchToUserId() {
	// if (permissions.getAdminID() > 0) {
	// switchToUser = permissions.getAdminID();
	// } else {
	// switchToUser = getClientSessionOriginalUserID();
	// }
	// }

	public String switchTo() throws Exception {
		loadPermissions(false);
		// add cookie before the switch so the original user id stays correct
		addClientSessionCookieToResponse(rememberMe, switchToUser);
		if (permissions.getUserId() == switchToUser) {
			// Switch back to myself
			user = getUser();
		} else {
			user = userDAO.find(switchToUser);
			if (user.getAccount().isAdmin() && !user.isGroup()) {
				// We're trying to login as another PICS user
				// Double check they also have the Dev permission too
				if (!permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
					logAttempt();
					addActionError("You must be a PICS Software Developer to switch to another PICS user.");
					return SUCCESS;
				}
			}
		}

		doSwitchToUser(switchToUser);
		username = permissions.getUsername();
		logAttempt();
		return REDIRECT;
	}

	private void doSwitchToUser(int userID) throws Exception {
		if (permissions.hasPermission(OpPerms.SwitchUser)) {
			int adminID = permissions.getUserId();
			boolean adminIsTranslator = permissions.hasPermission(OpPerms.Translator);

			user = userDAO.find(userID);
			permissions.login(user);
			LocaleController.setLocaleOfNearestSupported(permissions);
			permissions.setAdminID(adminID);

			if (adminIsTranslator) {
				permissions.setTranslatorOn();
			}
			password = "switchUser";
		} else {
			// TODO Verify the user has access to login
			permissions.setAccountPerms(user);
			password = "switchAccount";
		}
		ActionContext.getContext().getSession().put("permissions", permissions);
	}

	private Permissions permission() {
		if (permissionsForTest == null) {
			permissionsForTest = new Permissions();
		}
		return permissionsForTest;
	}

	public String login() throws Exception {
		logger.info("Normal login, via the actual Login.action page");

		if (ServletActionContext.getRequest().getCookies() == null) {
			addActionMessage(getText("Login.CookiesAreDisabled"));
			return SUCCESS;
		}

		permissions = permission();
		String error = canLogin();
		if (error.length() > 0) {
			logAttempt();
			addActionError(error);
			ActionContext.getContext().getSession().clear();
			return SUCCESS;
		}

		if ("reset".equals(button)) {
			user.setForcePasswordReset(true);
			user.setResetHash("");
		}

		logger.debug("logging in user");
		permissions.login(user);
		LocaleController.setLocaleOfNearestSupported(permissions);
		ActionContext.getContext().getSession().put("permissions", permissions);

		addClientSessionCookieToResponse(rememberMe, switchToUser);

		user.unlockLogin();
		user.setLastLogin(new Date());
		userDAO.save(user);

		setBetaTestingCookie();
		logAttempt();

		if (permissions.belongsToGroups() || permissions.isContractor()) {
			return setRedirectUrlPostLogin();
		} else {
			addActionMessage(getText("Login.NoGroupOrPermission"));
			return super.setUrlForRedirect("Login.action?button=logout");
		}
	}

	private String canLogin() throws Exception {
		try {
			user = userDAO.findName(username);
		} catch (NoResultException e) {
			user = null;
		}

		if (user == null) {
			return getText("Login.PasswordIncorrect");
		}
		if (Strings.isNotEmpty(key)) {
			if (user.getResetHash() == null || !user.getResetHash().equals(key)) {
				return getTextParameterized("Login.ResetCodeExpired", user.getUsername());
			}
		}
		if (user.isLocked()) {
			return getTextParameterized("Login.TooManyFailedAttempts");
		}
		if (!user.isEncryptedPasswordEqual(password)) {
			return passwordIsIncorrect();
		}

		if (!isUserActive()) {
			return getTextParameterized("Login.AccountNotActive");
		}

		return "";
	}

	private boolean isUserActive() {
		if (user.getAccount().isOperatorCorporate()) {
			if (!user.getAccount().getStatus().isActiveDemo()) {
				return false;
			}
		}
		if (user.getAccount().isContractor() && user.getAccount().getStatus().isDeleted()) {
			return false;
		}
		if (user.getIsActive() != YesNo.Yes) {
			return false;
		}
		return true;
	}

	private String passwordIsIncorrect() {
		user.setFailedAttempts(user.getFailedAttempts() + 1);
		if (user.getFailedAttempts() > MAX_FAILED_ATTEMPTS) {
			// Lock this user out for 1 hour
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.HOUR, 1);
			user.setFailedAttempts(0);
			user.setLockUntil(calendar.getTime());
			return getTextParameterized("Login.PasswordIncorrectAccountLocked", user.getUsername());
		}
		return getText("Login.PasswordIncorrect");
	}

	private String setRedirectUrlPostLogin() throws Exception {
		String redirectURL = getPreLoginUrl();

		if (Strings.isNotEmpty(redirectURL)) {
			return setUrlForRedirect(redirectURL);
		}

		redirectURL = getRedirectUrl();

		if (Strings.isNotEmpty(redirectURL)) {
			return setUrlForRedirect(redirectURL);
		}

		throw new Exception(getText("Login.NoPermissionsOrDefaultPage"));
	}

	private String getPreLoginUrl() {
		// Find out if the user previously timed out on a page, we'll forward
		// back there below
		String urlPreLogin = null;
		Cookie[] cookiesA = getRequest().getCookies();
		if (cookiesA != null) {
			for (int i = 0; i < cookiesA.length; i++) {
				if ("from".equals(cookiesA[i].getName())) {
					urlPreLogin = cookiesA[i].getValue();
					// Clear the cookie, now that we've used it once
					Cookie cookie = new Cookie("from", "");
					cookie.setMaxAge(ONE_SECOND);
					getResponse().addCookie(cookie);
				}
			}
		}
		return urlPreLogin;
	}

	private String getRedirectUrl() {
		String url = null;
		if (permissions.isContractor()) {
			ContractorAccount cAccount = (ContractorAccount) user.getAccount();

			ContractorRegistrationStep step = ContractorRegistrationStep.getStep(cAccount);
			url = step.getUrl();
		} else {
			if (user.isUsingDynamicReports()) {
				MenuComponent menu = MenuBuilder.buildMenubar(permissions);
				url = MenuBuilder.getHomePage(menu, permissions);
			} else {
				MenuComponent menu = PicsMenu.getMenu(permissions);
				url = PicsMenu.getHomePage(menu, permissions);
			}
		}
		return url;
	}

	private void setBetaTestingCookie() {
		boolean userBetaTester = isUserBetaTester();
		Cookie cookie = new Cookie("USE_BETA", userBetaTester + "");
		if (userBetaTester) {
			cookie.setMaxAge(365 * 24 * 60 * 60);
		} else {
			cookie.setMaxAge(0);
		}
		getResponse().addCookie(cookie);
	}

	private boolean isUserBetaTester() {
		String maxBetaLevel = propertyDAO.getProperty("BETA_maxLevel");
		int betaMax = NumberUtils.toInt(maxBetaLevel, 0);
		BetaPool betaPool = BetaPool.getBetaPoolByBetaLevel(betaMax);

		boolean userBetaTester = BetaPool.isUserBetaTester(permissions, betaPool);
		return userBetaTester;
	}

	private void logAttempt() throws Exception {
		if (user == null)
			return;

		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setLoginDate(new Date());
		loginLog.setRemoteAddress(getRequest().getRemoteAddr());
		String serverName = getRequest().getLocalName();
		UserAgentParser uap = new UserAgentParser(getRequest().getHeader("User-Agent"));
		loginLog.setBrowser(uap.getBrowserName() + " " + uap.getBrowserVersion());
		loginLog.setUserAgent(getRequest().getHeader("User-Agent"));
		if (isLiveEnvironment() || isBetaEnvironment()) {
			// Need computer name instead of www
			serverName = InetAddress.getLocalHost().getHostName();
		}

		loginLog.setServerAddress(serverName);
		loginLog.setSuccessful(permissions.isLoggedIn());
		loginLog.setUser(user);
		if (permissions.getAdminID() > 0)
			loginLog.setAdmin(new User(permissions.getAdminID()));

		loginLogDAO.save(loginLog);
	}

	/* GETTER & SETTERS */
	private HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSwitchToUser(int switchToUser) {
		this.switchToUser = switchToUser;
	}

	public int getSwitchToUser() {
		return switchToUser;
	}

	public void setUsern(String usern) {
		this.username = usern;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String setUrlForRedirect(String url) throws IOException {
		// TODO we need to sanitize this string or we'll allow a hacker to redirect to any URL
		if (!AjaxUtils.isAjax(getRequest())) {
			return super.setUrlForRedirect(url);
		}
		return BLANK;
	}

	public void setRedirect(boolean redirect) {
		if (redirect) {
			addActionMessage(getText("Login.Redirect"));
		}
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
}
