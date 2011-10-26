package com.picsauditing.access;

import java.io.IOException;
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
import com.picsauditing.PICS.DateBean;
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
				redirect("Login.action?msg=Cookies are disabled on your browser. Please open your "
						+ "browser settings and make sure cookies are enabled to log in to PICS");
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
				addActionMessage("Thank you for confirming your email address.");
			} catch (Exception e) {
				addActionError("Sorry, your account confirmation failed");
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
			cookie.setMaxAge(3600 * 24);
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

	private void printSession() {
		try {
			System.out.println("SessionID: " + getRequest().getSession().getId());
			System.out.println("CreationTime: " + getRequest().getSession().getCreationTime());
			System.out.println("LastAccessedTime: " + getRequest().getSession().getLastAccessedTime());
			System.out.println("MaxInactiveInterval: " + getRequest().getSession().getMaxInactiveInterval());
			System.out.println("Session Size: " + ActionContext.getContext().getSession().size());
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
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
		String result = Strings.validUserName(username);
		if (!result.equals("valid"))
			return result;

		try {
			user = userDAO.findName(username);
		} catch (NoResultException e) {
			user = null;
		}
		if (user == null)
			return "No account exists with that username";

		if (Strings.isEmpty(key)) {
			// After this point we should always have a user

			if (user.getAccount().isOperatorCorporate())
				if (!user.getAccount().getStatus().isActiveDemo())
					return user.getAccount().getName()
							+ " is no longer active.<br>Please contact PICS if you have any questions.";

			if (user.getAccount().isContractor() && user.getAccount().getStatus().isDeleted())
				return user.getAccount().getName()
						+ " has been deleted.<br>Please contact PICS if you have any questions.";

			if (user.getIsActive() != YesNo.Yes)
				return "This account for " + user.getAccount().getName()
						+ " is no longer active.<br>Please contact your administrator to reactivate it.";

			if (user.getLockUntil() != null && user.getLockUntil().after(new Date())) {
				return "This account is locked because of too many failed attempts. "
						+ "You will be able to try again in " + DateBean.prettyDate(user.getLockUntil());
			}
			if (Strings.isEmpty(password)) {
				return "You must enter a password";
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
					return "The password is not correct and the account has now been locked. <a href=\"http://www.picsorganizer.com/AccountRecovery.action?username="
							+ user.getUsername() + "\">Click here to reset your password</a>";
				}
				return "The password is not correct. You have " + (8 - user.getFailedAttempts())
						+ " attempts remaining before your account will be locked for one hour. "
						+ "<a href=\"http://www.picsorganizer.com/AccountRecovery.action?username="
						+ user.getUsername() + "\">Click here to reset your password</a>";
			}
		} else {
			if (user.getResetHash() == null) {
				return "Expired reset code. Try logging in below or <a href=\"http://www.picsorganizer.com/AccountRecovery.action?username="
						+ user.getUsername() + "\">Click here to send a new email</a>";
			}

			if (!user.getResetHash().equals(key)) {
				return "Expired reset code. Try logging in below or <a href=\"http://www.picsorganizer.com/AccountRecovery.action?username="
						+ user.getUsername() + "\">Click here to send a new email</a>";
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
					Cookie fromCookie = new Cookie("from", "");
					getResponse().addCookie(fromCookie);
				}
				if ("username".equals(cookiesA[i].getName()))
					cookieUsername = cookiesA[i].getValue();
			}
			if (!Strings.isEmpty(cookieUsername) && !cookieUsername.equals(permissions.getUsername())) {
				// If they are switching users, just send them back to the Home
				// Page
				cookieFromURL = "";
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
			url = step.getUrl(cAccount.getId());

		} else
			url = PicsMenu.getHomePage(menu, permissions);
		if (url == null)
			throw new Exception("No Permissions or Default Webpages found");

		redirect(url);
		return;
	}

	private void logAttempt() throws Exception {
		if (user == null)
			return;

		String remoteAddress = getRequest().getRemoteAddr();

		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setLoginDate(new Date());
		loginLog.setRemoteAddress(remoteAddress);
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
