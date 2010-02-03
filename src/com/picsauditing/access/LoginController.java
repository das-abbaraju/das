package com.picsauditing.access;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.Strings;

/**
 * Populate the permissions object in session with appropriate login credentials
 * and access/permission data
 * 
 * @author Glenn & Trevor
 * 
 */
@SuppressWarnings("serial")
public class LoginController extends PicsActionSupport {
	private User user;
	private String email;
	private String username;
	private String password;
	private String usern;
	private String key;
	private int switchToUser;

	protected UserDAO userDAO;
	protected UserLoginLogDAO loginLogDAO;

	public LoginController(UserDAO userDAO, UserLoginLogDAO loginLogDAO) {
		this.userDAO = userDAO;
		this.loginLogDAO = loginLogDAO;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		loadPermissions(false);

		if (button == null)
			return SUCCESS;

		if ("logout".equals(button)) {
			int adminID = permissions.getAdminID();
			permissions.clear();

			if (adminID > 0) {
				// Re login the admin on logout
				user = userDAO.find(adminID);

				permissions.login(user);
				postLogin();
				return SUCCESS;
			}

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
			}

			if (permissions.hasPermission(OpPerms.SwitchUser)) {
				int adminID = 0;
				if (permissions.getUserId() != switchToUser)
					adminID = permissions.getUserId();

				permissions.login(user);
				permissions.setAdminID(adminID);
				password = "switchUser";
			} else {
				// TODO Verify the user has access to login
				permissions.setAccountPerms(user);
				password = "switchAccount";
			}
			username = permissions.getUsername();
		} else {
			// Normal login, via the actual Login.action page
			permissions.clear();

			String error = canLogin();
			if (error.length() > 0) {
				logAttempt();
				addActionError(error);
				return SUCCESS;
			}

			if ("reset".equals(button)) {
				user.setForcePasswordReset(true);
				user.setResetHash("");
			}

			// /////////////////
			permissions.login(user);

			user.setLastLogin(new Date());
			user.getAccount().setLastLogin(new Date());
			userDAO.save(user);

			Cookie cookie = new Cookie("username", username);
			cookie.setMaxAge(3600 * 24);
			getResponse().addCookie(cookie);

			// TODO we should allow each account to set their own timeouts
			// ie..session.setMaxInactiveInterval(user.getAccountTimeout());
			if (permissions.isPicsEmployee())
				getRequest().getSession().setMaxInactiveInterval(3600);
		}

		ActionContext.getContext().getSession().put("permissions", permissions);
		logAttempt();
		postLogin();

		return SUCCESS;
	}

	/**
	 * Figure out if the current username/password is a valid user or account
	 * that can actually login. But don't actually login yet
	 * 
	 * @return
	 * @throws Exception
	 */
	private String canLogin() throws Exception {
		if (username == null || username.length() < 3)
			return "Enter a valid username";

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
					return "The password is not correct and the account has now been locked. <a href=\"http://www.picsauditing.com/AccountRecovery.action?username="
							+ user.getUsername() + "&button=Reset+Password\">Click here to reset your password</a>";
				}
				return "The password is not correct. You have " + (8 - user.getFailedAttempts())
						+ " attempts remaining before your account will be locked for one hour. "
						+ "<a href=\"http://www.picsauditing.com/AccountRecovery.action?username=" + user.getUsername()
						+ "&button=Reset+Password\">Click here to reset your password</a>";
			}
		} else {
			if (user.getResetHash() == null) {
				return "Expired reset code. Try logging in below or <a href=\"http://www.picsauditing.com/AccountRecovery.action?username="
						+ user.getUsername() + "\">Click here to send a new email</a>";
			}

			if (!user.getResetHash().equals(key)) {
				return "Expired reset code. Try logging in below or <a href=\"http://www.picsauditing.com/AccountRecovery.action?username="
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
				getResponse().sendRedirect(cookieFromURL);
				return;
			}
		}
		String url = null;
		if (permissions.isContractor() && !user.getAccount().getStatus().isActiveDemo()) {
			ContractorAccount cAccount = (ContractorAccount) user.getAccount();
			if (cAccount.getRiskLevel() == null)
				url = "ContractorRegistrationServices.action?id=" + cAccount.getId();
			else if (cAccount.getOperators().size() == 0)
				url = "ContractorFacilities.action?id=" + cAccount.getId();
			else if (!cAccount.isPaymentMethodStatusValid())
				url = "ContractorPaymentOptions.action?id=" + cAccount.getId();
			else
				url = "ContractorEdit.action?id=" + cAccount.getId();
		} else
			url = PicsMenu.getHomePage(menu, permissions);
		if (url == null)
			throw new Exception("No Permissions or Default Webpages found");

		getResponse().sendRedirect(url);
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

	// ////// GETTER & SETTERS ////////
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
}
