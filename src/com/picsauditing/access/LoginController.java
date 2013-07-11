package com.picsauditing.access;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.model.i18n.LanguageModel;
import com.picsauditing.security.CookieSupport;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.Strings;
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
import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Populate the permissions object in session with appropriate login credentials
 * and access/permission data
 */
@SuppressWarnings("serial")
public class LoginController extends PicsActionSupport {
	private static final int ONE_SECOND = 1;
	private static final Pattern TARGET_IP_PATTERN = Pattern.compile("^"
			+ CookieSupport.TARGET_IP_COOKIE_NAME + "-([^-]*)-81$");
	public static final String ACCOUNT_RECOVERY_ACTION = "AccountRecovery.action?username=";
	public static final String LOGIN_ACTION_BUTTON_LOGOUT = "Login.action?button=logout";
	public static final String DEACTIVATED_ACCOUNT_PAGE = "Deactivated.action";

	@Autowired
	private LoginService loginService;
	@Autowired
	protected UserLoginLogDAO loginLogDAO;
	@Autowired
	protected PermissionBuilder permissionBuilder;

	// used to inject mock permissions for testing
	private User user;
	private String email;
	private String username;
	private String password;
	private String key;
	private int switchToUser;
	private boolean rememberMe = false;
	private int sessionTimeout;

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	private static final int MAX_FAILED_ATTEMPTS = 6;

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
			permissions.setAccountPerms(user);
			password = "switchAccount";
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
			logAndMessageError(getTextParameterized("Login.ResetCodeExpired", e.getUsername()));
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
			logAndMessageError(getTextParameterized("Login.AccountNotActive", e.getUsername()));
			return ERROR;
		} catch (PasswordExpiredException e) {
			logAndMessageError(getText("Login.PasswordExpired"));
			setUrlForRedirect(ACCOUNT_RECOVERY_ACTION + e.getUsername());
			return REDIRECT;
		}

		// todo: Continue to move the rest of this method to services as needed.
		return doLogin();
	}

	private String loginNormally() throws Exception {
		if (!verifyCookiesAreEnabled()) {
			return ERROR;
		}

		try {
			user = loginService.loginNormally(username, password);

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
		logAttempt();

		if (permissions.belongsToGroups() || permissions.isContractor()) {
			return setRedirectUrlPostLogin();
		} else {
			addActionMessage(getText("Login.NoGroupOrPermission"));
			return super.setUrlForRedirect(LOGIN_ACTION_BUTTON_LOGOUT);
		}
	}

	private boolean logAndMessageError(String error) throws Exception {
		if (StringUtils.isNotEmpty(error)) {
			logAttempt();
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
		String redirectURL = null;
		switch (homePageType) {
			case PreLogin:
				redirectURL = preLoginUrl;
				break;
			case ContractorRegistrationStep:
				ContractorRegistrationStep step = ContractorRegistrationStep.getStep((ContractorAccount) user.getAccount());
				redirectURL = step.getUrl();
				break;
			case HomePage:
				if (user.isUsingVersion7Menus()) {
					MenuComponent menu = MenuBuilder.buildMenubar(permissions);
					redirectURL = MenuBuilder.getHomePage(menu, permissions);
				} else {
					MenuComponent menu = PicsMenu.getMenu(permissions);
					redirectURL = PicsMenu.getHomePage(menu, permissions);
				}
				break;
			case Deactivated:
				redirectURL = DEACTIVATED_ACCOUNT_PAGE;
				break;
            case Declined:
                // per PICS-10995 - declined is an internal status and doesn't need to be shown on a special page
                // just show them deactivated
                redirectURL = DEACTIVATED_ACCOUNT_PAGE;
                break;
		}

		return redirectURL;
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

	private void logAttempt() throws Exception {
		if (user == null) {
			return;
		}

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
		loginLog.setUser(user);

		String targetIp = extractTargetIpFromCookie();
		if (!Strings.isEmpty(targetIp)) {
			loginLog.setTargetIP(targetIp);
		}

		Permissions permissions = permissions();
		loginLog.setSuccessful(permissions.isLoggedIn());
		if (permissions.getAdminID() > 0) {
			loginLog.setAdmin(new User(permissions.getAdminID()));
		}

		loginLogDAO.save(loginLog);
	}

	private String extractTargetIpFromCookie() {
		List<Cookie> matchingCookies = CookieSupport.cookiesFromRequestThatStartWith(getRequest(),
				CookieSupport.TARGET_IP_COOKIE_NAME);
		for (Cookie cookie : matchingCookies) {
			Matcher matcher = TARGET_IP_PATTERN.matcher(cookie.getName());
			if (matcher.matches()) {
				return matcher.group(1);
			}
		}
		return "";
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

}
