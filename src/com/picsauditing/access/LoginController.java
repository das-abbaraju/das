package com.picsauditing.access;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.LocaleController;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

/**
 * Populate the permissions object in session with appropriate login credentials
 * and access/permission data
 */
@SuppressWarnings("serial")
public class LoginController extends PicsActionSupport {

	private static final int ONE_SECOND = 1;
	private static final int SECONDS_PER_HOUR = 3600;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected UserLoginLogDAO loginLogDAO;
	@Autowired
	protected AppPropertyDAO appPropertyDAO;

	private User user;
	private String email;
	private String username;
	private String password;
	private String key;
	private int switchToUser;
	private int switchServerToUser;

	private final Logger LOG = LoggerFactory.getLogger(LoginController.class);

	@Anonymous
	@Override
	public String execute() throws Exception {
		if (button == null) {
			// ServletActionContext.getRequest().getSession().invalidate();
			return SUCCESS;
		}
		loadPermissions(false);

		if ("logout".equals(button)) {
			// The msg parameter is passed on Permissions.java when a session
			// has timed out and login is required again.
			// A cookie is set in this method, and if this msg has been passed,
			// and cookies are still non-existant,
			// then cookies are disabled.
			// Note: Sessions are saved even if cookies are disabled on
			// Mozilla 5 and others.
			if (ServletActionContext.getRequest().getCookies() == null) {
				addActionMessage(getText("Login.CookiesAreDisabled"));

				return SUCCESS;
			}

			int adminID = permissions.getAdminID();
			permissions.clear();

			if (adminID > 0) {

				// Re login the admin on logout
				user = userDAO.find(adminID);

				permissions.login(user);
				permissions.getToggles().putAll(getApplicationToggles());
				LocaleController.setLocaleOfNearestSupported(permissions);
				if (isLiveEnvironment()) {
					if (ActionContext.getContext().getSession().get("redirect") != null) {
						if (ActionContext.getContext().getSession().get("redirect").equals("true")) {
							// reset beta cookie
							setBetaTestingCookie();
							// redirect to original site.
							setUrlForRedirect("http://www.picsorganizer.com");
						}
						ActionContext.getContext().getSession().remove("redirect");
					}
				}
				postLogin();
				return REDIRECT;
			}

			ActionContext.getContext().getSession().clear();
			// ServletActionContext.getRequest().getSession().invalidate();
			// ServletActionContext.getRequest().getSession().removeAttribute("permissions");

			return SUCCESS;
		}

		if ("confirm".equals(button)) {
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

		// Autologin functionality if the reset button is passed, otherwise
		// perform
		// other login procedures
		if (switchToUser > 0) {
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

			switchToUser(switchToUser);
			username = permissions.getUsername();
		} else {
			// Normal login, via the actual Login.action page

			PicsLogger.start("Login", "Normal login");
			permissions.clear();
			String error = canLogin();
			if (error.length() > 0) {
				logAttempt();
				addActionError(error);
				// ServletActionContext.getRequest().getSession().invalidate();
				ActionContext.getContext().getSession().clear();
				return SUCCESS;
			}

			if ("reset".equals(button)) {
				user.setForcePasswordReset(true);
				user.setResetHash("");
			}

			// /////////////////
			PicsLogger.log("logging in user: " + user.getUsername());
			permissions.login(user);
			LocaleController.setLocaleOfNearestSupported(permissions);
			permissions.getToggles().putAll(getApplicationToggles());

			user.setLastLogin(new Date());
			userDAO.save(user);

			Cookie cookie = new Cookie("username", username);
			cookie.setMaxAge(SECONDS_PER_HOUR * 24);
			getResponse().addCookie(cookie);
			// check to see if there is switchtouseid exist, which comes from
			// redirect from another server. if it does, then after log in,
			// redirect it.
			if (switchServerToUser > 0) {
				switchToUser(switchServerToUser);
				ActionContext.getContext().getSession().put("redirect", "true");
			}

			PicsLogger.stop();
		}

		if (permissions.isLoggedIn())
			ActionContext.getContext().getSession().put("permissions", permissions);
		else
			ActionContext.getContext().getSession().clear();
		logAttempt();

		if (permissions.getGroups().size() > 0 || permissions.isContractor()) {
			postLogin();
			return REDIRECT;
		} else {
			addActionMessage(getText("Login.NoGroupOrPermission"));

			return super.setUrlForRedirect("Login.action?button=logout");
		}
	}

	private void switchToUser(int userID) throws Exception {
		if (permissions.hasPermission(OpPerms.SwitchUser)) {
			int adminID = 0;
			if (permissions.getUserId() != switchServerToUser)
				adminID = permissions.getUserId();

			boolean translator = (adminID > 0 && permissions.hasPermission(OpPerms.Translator));
			user = userDAO.find(userID);
			permissions.login(user);
			permissions.getToggles().putAll(getApplicationToggles());
			LocaleController.setLocaleOfNearestSupported(permissions);
			permissions.setAdminID(adminID);
			if (translator)
				permissions.setTranslatorOn();
			password = "switchUser";
		} else {
			// TODO Verify the user has access to login
			permissions.setAccountPerms(user);
			password = "switchAccount";
		}
	}

	/**
	 * Method to log in via an ajax overlay
	 * 
	 * @return
	 * @throws Exception
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
	 * 
	 * @return
	 */
	@Anonymous
	public String overlay() {
		setRedirect(true);
		return "overlay";
	}

	/**
	 * Figure out if the current username/password is a valid user or account
	 * that can actually login. But don't actually login yet
	 * 
	 * @return
	 * @throws Exception
	 */
	private String canLogin() throws Exception {
		// TODO: Move this into User-validation.xml and use struts 2 for this
		// validation
		if (Strings.isEmpty(username))
			return getText("User.username.error.Empty");
		else if (username.length() < 3)
			return getText("User.username.error.Short");
		else if (username.length() > 100)
			return getText("User.username.error.Long");
		else if (username.contains(" "))
			return getText("User.username.error.Space");
		else if (!username.matches("^[a-zA-Z0-9+._@-]{3,50}$"))
			return getText("User.username.error.Special");

		try {
			user = userDAO.findName(username);
		} catch (NoResultException e) {
			user = null;
		}
		if (user == null)
			return getText("Login.NoAccountExistsWithUsername");

		if (Strings.isEmpty(key)) {
			// After this point we should always have a user

			if (user.getAccount().isOperatorCorporate())
				if (!user.getAccount().getStatus().isActiveDemo())
					return getTextParameterized("Login.NoLongerActive", user.getAccount().getName());

			if (user.getAccount().isContractor() && user.getAccount().getStatus().isDeleted())
				return getTextParameterized("Login.AccountDeleted", user.getAccount().getName());

			if (user.getIsActive() != YesNo.Yes)
				return getTextParameterized("Login.AccountNotActive", user.getAccount().getName());

			if (user.getLockUntil() != null && user.getLockUntil().after(new Date())) {
				Date now = new Date();
				long diff = user.getLockUntil().getTime() - now.getTime();
				int minutes = (int) (diff / (1000 * 60));

				return getTextParameterized("Login.TooManyFailedAttempts", minutes);
			}

			if (Strings.isEmpty(password)) {
				return getText("Login.MustEnterPassword");
			}

			if (!user.isEncryptedPasswordEqual(password)) {
				user.setFailedAttempts(user.getFailedAttempts() + 1);
				// TODO parameterize this 7 here
				if (user.getFailedAttempts() > 7) {
					// Lock this user out for 1 hour
					Calendar calendar = Calendar.getInstance();
					calendar.add(Calendar.HOUR, 1);
					user.setFailedAttempts(0);
					user.setLockUntil(calendar.getTime());
					return getTextParameterized("Login.PasswordIncorrectAccountLocked", user.getUsername());
				}

				return getTextParameterized("Login.PasswordIncorrectAttemptsRemaining", (8 - user.getFailedAttempts()),
						user.getUsername());
			}
		} else {
			if (user.getResetHash() == null || !user.getResetHash().equals(key)) {
				return getTextParameterized("Login.ResetCodeExpired", user.getUsername());
			}
		}

		user.setFailedAttempts(0);
		user.setLockUntil(null); // it's no longer locked

		// We are now ready to actually do the login (doLogin)
		return "";
	}

	private Map<String, String> getApplicationToggles() {
		List<AppProperty> toggleList = propertyDAO.getPropertyList("WHERE property LIKE 'Toggle.%'");
		Map<String, String> toggles = new HashMap<String, String>();
		if (toggleList != null) {
			for (int i = 0; i < toggleList.size(); i++) {
				toggles.put(toggleList.get(i).getProperty(), toggleList.get(i).getValue());
			}
		}
		return toggles;
	}

	/**
	 * After we're logged in, now what should we do?
	 */
	private void postLogin() throws Exception {
		// Find out if the user previously timed out on a page, we'll forward
		// back there below

		Cookie[] cookiesA = getRequest().getCookies();
		if (cookiesA != null) {
			String cookieFromURL = "";
			String cookieUsername = "";
			for (int i = 0; i < cookiesA.length; i++) {
				if ("from".equals(cookiesA[i].getName())) {
					cookieFromURL = cookiesA[i].getValue();
					// Clear the cookie, now that we've used it once
					Cookie cookie = new Cookie("from", "");
					cookie.setMaxAge(ONE_SECOND);
					getResponse().addCookie(cookie);
				}

				if ("username".equals(cookiesA[i].getName()))
					cookieUsername = cookiesA[i].getValue();
			}

			if (!Strings.isEmpty(cookieUsername) && !cookieUsername.equals(permissions.getUsername())) {
				// If they are switching users, just send them back to the Home
				// Page
				cookieFromURL = "";
				// Clear the username cookie
				Cookie cookie = new Cookie("username", "");
				cookie.setMaxAge(ONE_SECOND);
				getResponse().addCookie(cookie);
			}

			if (switchToUser == 0 && switchServerToUser == 0)
				setBetaTestingCookie();

			if (cookieFromURL.length() > 0) {
				setUrlForRedirect(cookieFromURL);
				return;
			}
		}

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

		if (url == null)
			throw new Exception(getText("Login.NoPermissionsOrDefaultPage"));

		setUrlForRedirect(url);
		return;
	}

	private void setBetaTestingCookie() {
		String maxBetaLevel = appPropertyDAO.getProperty("BETA_maxLevel");
		int betaMax = NumberUtils.toInt(maxBetaLevel, 0);
		BetaPool betaPool = BetaPool.getBetaPoolByBetaLevel(betaMax);

		boolean userBetaTester = BetaPool.isUserBetaTester(permissions, betaPool);

		Cookie cookie = new Cookie("USE_BETA", userBetaTester + "");
		if (userBetaTester) {
			cookie.setMaxAge(365 * 24 * 60 * 60);
		} else {
			cookie.setMaxAge(0);
		}
		getResponse().addCookie(cookie);
	}

	public void printCookie() {
		Cookie[] cookiesA = getRequest().getCookies();
		if (cookiesA != null) {
			for (int i = 0; i < cookiesA.length; i++) {
				LOG.error("cookie name " + cookiesA[i].getName() + " cookie value " + cookiesA[i].getValue());
			}
		}
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

	/* GElTTER & SETTERS */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setLoginLogDAO(UserLoginLogDAO loginLogDAO) {
		this.loginLogDAO = loginLogDAO;
	}

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

	public void setSwitchServerToUser(int switchServerToUser) {
		this.switchServerToUser = switchServerToUser;
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
}
