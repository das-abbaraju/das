package com.picsauditing.access;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
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
import com.picsauditing.util.LocaleController;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

/**
 * Populate the permissions object in session with appropriate login credentials and access/permission data
 */
@SuppressWarnings("serial")
public class LoginController extends PicsActionSupport {

	private static final int ONE_SECOND = 1;
	private static final int ONE_HOUR = 3600;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected UserLoginLogDAO loginLogDAO;

	private User user;
	private String email;
	private String username;
	private String password;
	private String key;
	private int switchToUser;

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
			if (ServletActionContext.getRequest().getCookies() == null
					&& ServletActionContext.getRequest().getParameter("msg") != null) {
				redirect("Login.action?msg=" + getText("Login.CookiesAreDisabled"));
			}

			int adminID = permissions.getAdminID();
			permissions.clear();

			if (adminID > 0) {
				// Re login the admin on logout
				user = userDAO.find(adminID);

				permissions.login(user);
				LocaleController.setLocaleOfNearestSupported(permissions);
				postLogin();
				return SUCCESS;
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

			if (permissions.hasPermission(OpPerms.SwitchUser)) {
				int adminID = 0;
				if (permissions.getUserId() != switchToUser)
					adminID = permissions.getUserId();

				boolean translator = (adminID > 0 && permissions.hasPermission(OpPerms.Translator));

				permissions.login(user);
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

			user.setLastLogin(new Date());
			userDAO.save(user);

			Cookie cookie = new Cookie("username", username);
			cookie.setMaxAge(ONE_HOUR * 24);
			getResponse().addCookie(cookie);

			PicsLogger.stop();
		}

		if (permissions.isLoggedIn())
			ActionContext.getContext().getSession().put("permissions", permissions);
		else
			ActionContext.getContext().getSession().clear();
		logAttempt();
		postLogin();

		return SUCCESS;
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
		String result = execute();
		if ("success".equals(result)) {
			json = new JSONObject();
			json.put("loggedIn", permissions.isLoggedIn());
			return JSON;
		}

		return BLANK;
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
	 * Figure out if the current username/password is a valid user or account that can actually login. But don't
	 * actually login yet
	 * 
	 * @return
	 * @throws Exception
	 */
	private String canLogin() throws Exception {
		// TODO: Move this into User-validation.xml and use struts 2 for this validation
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

	/**
	 * After we're logged in, now what should we do?
	 */
	private void postLogin() throws Exception {
		MenuComponent menu = PicsMenu.getMenu(permissions);

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

			if (cookieFromURL.length() > 0) {
				redirect(cookieFromURL);
				return;
			}
		}
		String url = null;
		if (permissions.isContractor()) {
			ContractorAccount cAccount = (ContractorAccount) user.getAccount();

			ContractorRegistrationStep step = ContractorRegistrationStep.getStep(cAccount);
			url = step.getUrl();

		} else
			url = PicsMenu.getHomePage(menu, permissions);
		if (url == null)
			throw new Exception(getText("Login.NoPermissionsOrDefaultPage"));

		redirect(url);
		return;
	}

	private void logAttempt() throws Exception {
		if (user == null)
			return;

		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setLoginDate(new Date());
		loginLog.setRemoteAddress(getRequest().getRemoteHost());
		
		String serverName = getRequest().getServerName();
		if (isLiveEnvironment()) {
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
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	public void setLoginLogDAO(UserLoginLogDAO loginLogDAO) {
		this.loginLogDAO = loginLogDAO;
	}

	private HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
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

	public void setSwitchToUser(int switchToUser) {
		this.switchToUser = switchToUser;
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
	public String redirect(String url) throws IOException {
		if (!AjaxUtils.isAjax(getRequest())) {
			return super.redirect(url);
		}
		return BLANK;
	}

	public void setRedirect(boolean redirect) {
		if (redirect) {
			addActionMessage(getText("Login.Redirect"));
		}
	}
}
