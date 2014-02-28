package com.picsauditing.access;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.dao.ReportUserDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.security.CookieSupport;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Populate the permissions object in session with appropriate login credentials
 * and access/permission data
 */
@SuppressWarnings("serial")
public class LoginController extends PicsActionSupport {
	private static final int ONE_SECOND = 1;
	public static final String ACCOUNT_RECOVERY_ACTION = "AccountRecovery.action?username=";
	public static final String LOGIN_ACTION_BUTTON_LOGOUT = "Login.action?button=logout";
	public static final String DEACTIVATED_ACCOUNT_PAGE = "Deactivated.action";

	// FOR TESTING ONLY
	protected static ReportUserDAO reportUserDAO;

	@Autowired
	private LoginService loginService;
	@Autowired
	private com.picsauditing.employeeguard.services.LoginService egLoginService;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	protected PermissionBuilder permissionBuilder;
	@Autowired
	private ProfileService profileService;

	private User user;
	private String email;
	private String username;
	private String password;
	private String key;
	private int switchToUser;
	private boolean rememberMe = false;
	private int sessionTimeout;

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Anonymous
	@Override
	public String execute() throws Exception {
		if (button == null) {
			if (sessionCookieIsValidAndNotExpired()) {
				switchToUser = getClientSessionUserID();
				return switchTo();
			} else {
				clearPicsOrgCookie();

				if (ActionContext.getContext().getLocale() == null) {
					ExtractBrowserLanguage languageUtility = new ExtractBrowserLanguage(getRequest(), supportedLanguages
							.getVisibleLanguages());
					ActionContext.getContext().setLocale(languageUtility.getBrowserLocale());
				}

				return SUCCESS;
			}
		} else if ("confirm".equals(button)) {
			return confirm();
		} else if ("logout".equals(button)) {
			return logout();
		} else if ("switchBack".equalsIgnoreCase(button)) {
			return switchBack();
		} else if ("reset".equalsIgnoreCase(button)) {
			return loginForResetPassword();
		} else if (switchToUser > 0) {
			return switchTo();
		} else {
			return loginNormally();
		}
	}

	/**
	 * Method to log in via an ajax overlay
	 */
	@SuppressWarnings("unchecked")
	@Anonymous
	public String ajax() throws Exception {
		if (!AjaxUtils.isAjax(getRequest())) {
			return BLANK;
		}

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

		Locale userLocale = permissions.getLocale();
		permissions.clear();
		// If the user was using a beta language, default back to English because
		// only stable languages are available on the main language dropdown
		if (userLocale != null && !supportedLanguages.isLanguageVisible(userLocale)) {
			// TODO: Does this also need to happen in any other login situation?
			ActionContext.getContext().setLocale(LanguageModel.ENGLISH);
		}

		invalidateSession();
		clearPicsOrgCookie();

		return SUCCESS;
	}

	@Anonymous
	@SuppressWarnings("unchecked")
	public String sessionLogout() throws Exception {
		logout();

		json = new JSONObject();
		json.put("referer", getReferer());

		return JSON;
	}

	private String switchBack() throws Exception {
		loadPermissions(false);
		switchToUser = 0;

		int originalUser = getClientSessionOriginalUserID();
		if (originalUser > 0) {
			user = userDAO.find(originalUser);
			permissions = permissionBuilder.login(user);
			addClientSessionCookieToResponse(isRememberMeSetInCookie(), 0);
			permissions.setAdminID(0);
			permissions.setSwitchedToUserName(null);
			ActionContext.getContext().getSession().put("permissions", permissions);
		}

		return setRedirectUrlPostLogin();
	}

	private String switchTo() throws Exception {
		loadPermissions(false);
		// add cookie before the switch so the original user id stays correct
		addClientSessionCookieToResponse(isRememberMeSetInCookie(), switchToUser);
		if (permissions.getUserId() == switchToUser) {
			// Switch back to myself
			user = getUser();
		} else {
			user = userDAO.find(switchToUser);
			if (user.getAccount().isAdmin() && !user.isGroup()) {
				// We're trying to login as another PICS user
				// Double check they also have the Dev permission too
				if (!permissions.hasPermission(OpPerms.DevelopmentEnvironment)) {
					logSwitchToAttempt(user);
					addActionError("You must be a PICS Software Developer to switch to another PICS user.");
					return SUCCESS;
				}
			}
		}

		doSwitchToUser(switchToUser);

		if (hasActionErrors()) {
			throw new AccountNotFoundException();
		}

		username = permissions.getUsername();
		logSwitchToAttempt(user);
		return setRedirectUrlPostLogin();
	}

	private void doSwitchToUser(int userID) throws Exception {
		if (permissions.hasPermission(OpPerms.SwitchUser)) {
			int adminID = permissions.getUserId();
			int maxAge = permissions.getRememberMeTimeInSeconds();
			boolean adminIsTranslator = permissions.hasPermission(OpPerms.Translator);

			user = userDAO.find(userID);
			permissions = permissionBuilder.login(user);
			permissions.setAdminID(adminID);
			permissions.setRememberMeTimeInSeconds(maxAge);
			if (adminID != userID) {
				permissions.setSwitchedToUserName(user.getName());
			}

			if (adminIsTranslator) {
				permissions.setTranslatorOn();
			}
			password = "switchUser";
		} else {
			// TODO Verify the user has access to login
			if (user != null) {
				permissions.setAccountPerms(user);
				password = "switchAccount";
			} else {
				AppUser appUser = appUserDAO.findByAppUserID(permissions.getAppUserID());
				if (appUser != null) {
					Profile profile = profileService.findByAppUserId(appUser.getId());
					permissions.login(appUser, profile);
				} else {
					setActionErrorHeader(getText("Login.Failed"));
					logAndMessageError(getText("Login.PasswordIncorrect"));
				}
			}
		}
		ActionContext.getContext().getSession().put("permissions", permissions);
	}

	private Permissions permissions() {
		if (permissions == null) {
			permissions = new Permissions();
		}

		return permissions;
	}

	@RequiredPermission(value = OpPerms.DevelopmentEnvironment)
	public String setSessionTimeout() {
		HttpSession session = ServletActionContext.getRequest().getSession(false);
		if (session != null) {
			session.setMaxInactiveInterval(sessionTimeout);
		}
		return SUCCESS;
	}

	private String loginForResetPassword() throws Exception {
		if (!verifyCookiesAreEnabled()) {
			return ERROR;
		}

		try {
			user = loginService.loginForResetPassword(username, key);

		} catch (InvalidResetKeyException e) {
			setActionErrorHeader(getText("Login.Failed"));
			logAndMessageError(getTextParameterized("Login.ResetCodeHasExpired", getResendEmailAction(getRequestHost(), e.getUsername())));
			return ERROR;
		} catch (AccountNotFoundException e) {
			setActionErrorHeader(getText("Login.Failed"));
			logAndMessageError(getText("Login.PasswordIncorrect"));
			return ERROR;
		} catch (AccountLockedException e) {
			setActionErrorHeader(getText("Login.Failed"));
			logAndMessageError(getTextParameterized("Login.TooManyFailedAttempts"));
			return ERROR;
		} catch (AccountInactiveException e) {
			logAndMessageError(getTextParameterized("Login.AccountNotActive", getRequestHost(), e.getUsername()));
			return ERROR;
		} catch (PasswordExpiredException e) {
			logAndMessageError(getText("Login.PasswordExpired"));
			setUrlForRedirect(ACCOUNT_RECOVERY_ACTION + e.getUsername());
			return REDIRECT;
		}

		// todo: Continue to move the rest of this method to services as needed.
		return doLogin();
	}

    private String getResendEmailAction(String server, String userName) {
        String serverName = server.replace("http://", "https://");

        Map<String, Object> parameters = new TreeMap<>();
        parameters.put("username", userName);

        URLUtils urlUtils = new URLUtils();

        return serverName + urlUtils.getActionUrl("AccountRecovery", parameters);
    }

	private String loginNormally() throws Exception {
		if (!verifyCookiesAreEnabled()) {
			return ERROR;
		}

		try {
			AppUser appUser = appUserDAO.findByUserName(username);
			if (appUser == null) {
				setActionErrorHeader(getText("Login.Failed"));
				logAndMessageError(getText("Login.PasswordIncorrect"));
				return ERROR;
			} else {
				user = userDAO.findUserByAppUserID(appUser.getId());
			}

			if (user != null) {
				user = loginService.loginNormally(user, username, password);
			} else {
				Profile profile = profileService.findByAppUserId(appUser.getId());
				if (profile == null) {
					setActionErrorHeader(getText("Login.Failed"));
					logAndMessageError(getText("Login.PasswordIncorrect"));
					return ERROR;
				} else {
					JSONObject result = egLoginService.loginViaRest(username, password);
					permissions = permissionBuilder.login(appUser, profile);

					if ("SUCCESS".equals(result.get("status").toString())) {
						doSetCookie(result.get("cookie").toString(), 10);
						return setUrlForRedirect("/employee-guard/employee/dashboard");
					} else {
						setActionErrorHeader(getText("Login.Failed"));
						logAndMessageError(getText("Login.PasswordIncorrect"));
						return ERROR;
					}
				}
			}
		} catch (AccountNotFoundException e) {
			setActionErrorHeader(getText("Login.Failed"));
			logAndMessageError(getText("Login.PasswordIncorrect"));
			return ERROR;
		} catch (AccountLockedException e) {
			setActionErrorHeader(getText("Login.Failed"));
			logAndMessageError(getTextParameterized("Login.TooManyFailedAttempts"));
			return ERROR;
		} catch (AccountInactiveException e) {
			logAndMessageError(getTextParameterized("Login.AccountNotActive", e.getUsername()));
			return ERROR;
		} catch (FailedLoginException e) {
			setActionErrorHeader(getText("Login.Failed"));
			logAndMessageError(getText("Login.PasswordIncorrect"));
			return ERROR;
		} catch (FailedLoginAndLockedException e) {
			setActionErrorHeader(getText("Login.Failed"));
			logAndMessageError(getTextParameterized("Login.PasswordIncorrectAccountLocked", e.getUsername()));
			return ERROR;
		} catch (PasswordExpiredException e) {
			logAndMessageError(getText("Login.PasswordExpired"));
			setUrlForRedirect(ACCOUNT_RECOVERY_ACTION + e.getUsername());
			return REDIRECT;
		}

		// todo: Continue to move the rest of this method to services as needed.
		return doLogin();
	}

	private String doLogin() throws Exception {
		permissions = permissionBuilder.login(user);
		ActionContext.getContext().getSession().put("permissions", permissions);

		addClientSessionCookieToResponse(rememberMe, switchToUser);

		updateUserForSuccessfulLogin();

		setBetaTestingCookie();
		logCredentialLoginAttempt(user);

		if (permissions.belongsToGroups() || permissions.isContractor()) {
			return setRedirectUrlPostLogin();
		} else {
			addActionMessage(getText("Login.NoGroupOrPermission"));
			return super.setUrlForRedirect(LOGIN_ACTION_BUTTON_LOGOUT);
		}
	}

	private boolean logAndMessageError(String error) throws Exception {
		if (StringUtils.isNotEmpty(error)) {
			logCredentialLoginAttempt(user);
			addActionError(error);
			ActionContext.getContext().getSession().clear();
			return true;
		}
		return false;
	}

	private boolean verifyCookiesAreEnabled() {
		if (ServletActionContext.getRequest().getCookies() == null) {
			addActionMessage(getText("Login.CookiesAreDisabled"));
			return false;
		}
		return true;
	}

	private void updateUserForSuccessfulLogin() {
		user.unlockLogin();
		user.setLastLogin(new Date());
		userDAO.save(user);
	}

	private String setRedirectUrlPostLogin() throws Exception {
		String preLoginUrl = getPreLoginUrl();
		HomePageType homePageType = loginService.postLoginHomePageTypeForRedirect(preLoginUrl, user);
		String redirectURL = determineRedirectUrlFromHomePageType(preLoginUrl, homePageType);

		if (Strings.isNotEmpty(redirectURL)) {
			return setUrlForRedirect(redirectURL);
		}
		throw new Exception(getText("Login.NoPermissionsOrDefaultPage"));
	}

	private String determineRedirectUrlFromHomePageType(String preLoginUrl, HomePageType homePageType) {
		switch (homePageType) {
			case PreLogin:
				return preLoginUrl;
			case ContractorRegistrationStep:
				ContractorRegistrationStep step = ContractorRegistrationStep.getStep((ContractorAccount) user.getAccount());
				return step.getUrl();
			case HomePage:
				if (user.isUsingVersion7Menus()) {
					MenuBuilder.reportUserDAO = setReportUserDAO();
					return MenuBuilder.getHomePage(permissions);
				} else {
					MenuComponent menu = PicsMenu.getMenu(permissions);
					return PicsMenu.getHomePage(menu, permissions);
				}
			case Deactivated:
				return DEACTIVATED_ACCOUNT_PAGE;
			case Declined:
				// per PICS-10995 - declined is an internal status and doesn't need to be shown on a special page
				// just show them deactivated
				return DEACTIVATED_ACCOUNT_PAGE;
			case EmployeeGUARD:
				return "/employee-guard/employee/dashboard";
			default:
				return null;
		}
	}

	private String getPreLoginUrl() {
		// Find out if the user previously timed out on a page, so we can forward to it if appropriate for the user
		String urlPreLogin = null;
		Cookie cookie = CookieSupport.cookieFromRequest(getRequest(), CookieSupport.PRELOGIN_URL_COOKIE_NAME);
		if (cookie != null) {
			// PICS-7659: "/Home.action causing a loop
			urlPreLogin = cookie.getValue().replaceAll("\"", "");
			// Clear the cookie, now that we've used it once
			Cookie resetCookie = new Cookie(CookieSupport.PRELOGIN_URL_COOKIE_NAME, "");
			resetCookie.setMaxAge(ONE_SECOND);
			getResponse().addCookie(resetCookie);
		}
		return urlPreLogin;
	}

	private void setBetaTestingCookie() {
		boolean userBetaTester = isUserBetaTester();
		Cookie cookie = new Cookie(CookieSupport.USE_BETA_COOKIE_NAME, userBetaTester + "");
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

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public static ReportUserDAO setReportUserDAO() {
		if (reportUserDAO == null)
			return SpringUtils.getBean("ReportUserDAO");
		return reportUserDAO;
	}
}
