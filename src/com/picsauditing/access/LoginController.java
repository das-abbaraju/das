package com.picsauditing.access;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;

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
	private int switchToUser;

	protected UserDAO userDAO;
	protected UserLoginLogDAO loginLogDAO;

	public LoginController(UserDAO userDAO, UserLoginLogDAO loginLogDAO) {
		this.userDAO = userDAO;
		this.loginLogDAO = loginLogDAO;
	}

	@Override
	public String execute() throws Exception {
		loadPermissions(false);
		
		if (button == null)
			return SUCCESS;
		
		if ("logout".equals(button)) {
			int adminID = permissions.getAdminID();
			permissions.clear();
			getRequest().getSession().invalidate();
			if (adminID > 0) {
				// Re login the admin on logout
				user = userDAO.find(adminID);
				
				this.doLogin(false);
				postLogin();
				return SUCCESS;
			}

			//getResponse().sendRedirect("Login.action");
			return SUCCESS;
		}
		
		if ("forgot".equals(button)) {
		}
		
		if ("confirm".equals(button)) {
			// TODO accountBean.sendPasswordEmail(email);
			//aBean.updateEmailConfirmedDate(username_email);
			addActionMessage("Thank you for confirming your email address. Please login to access the site.");
			return button;

		}
		
		// Login the user
		if (switchToUser > 0) {
			permissions.tryPermission(OpPerms.SwitchUser);
			int myCurrentID = permissions.getUserId();

			user = userDAO.find(switchToUser);

			permissions.clear();
			permissions.setAdminID(myCurrentID);
			
			username = user.getUsername();
			password = "switchUser";

			doLogin(false);
		} else {
			loadPermissions(false);
			permissions.clear();

			String error = canLogin();
			if (error.length() > 0) {
				logAttempt();
				addActionError(error);
				return SUCCESS;
			}

			// /////////////////
			doLogin(true);
		}
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

		// After this point we should always have a user

		if (user.getAccount().getActive() != 'Y')
			return "This account is no longer active.<br>Please contact PICS to activate your company.";

		if (user.getIsActive() != YesNo.Yes)
			return "This user account is no longer active.<br>Please contact your administrator to reactivate it.";

		if (user.getLockUntil() != null && user.getLockUntil().after(new Date()))
			return "This account is locked because of too many failed attempts";

		if (!user.getPassword().equals(password)) {
			user.setFailedAttempts(user.getFailedAttempts() + 1);
			// TODO parameterize this 7 here
			if (user.getFailedAttempts() > 7) {
				// Lock this user out for 1 hour
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.HOUR, 1);
				user.setFailedAttempts(0);
				user.setLockUntil(calendar.getTime());
				return "The password is not correct and the account has now been locked";
			}
			return "The password is not correct";
		}
		user.setFailedAttempts(0);
		user.setLockUntil(null); // it's no longer locked

		// We are now ready to actually do the login (doLogin)
		return "";
	}

	/**
	 * Perform the actual login process...store any info in the session that
	 * will be required in later pages
	 */
	private void doLogin(boolean updateLastLogin) throws Exception {
		permissions.login(user);
		if (updateLastLogin) {
			user.setLastLogin(new Date());
			user.getAccount().setLastLogin(new Date());
			userDAO.save(user);
		}

		// TODO we should allow each account to set their own timeouts
		// ie..session.setMaxInactiveInterval(user.getAccountTimeout());
		if (permissions.isPicsEmployee())
			getRequest().getSession().setMaxInactiveInterval(3600);
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
			String fromURL = "";
			for (int i = 0; i < cookiesA.length; i++) {
				if ("from".equals(cookiesA[i].getName())) {
					fromURL = cookiesA[i].getValue();
					// Clear the cookie, now that we've used it once
					Cookie fromCookie = new Cookie("from", "");
					getResponse().addCookie(fromCookie);
					if (fromURL.length() > 0) {
						getResponse().sendRedirect(fromURL);
						return;
					}
				}
			}
		}

		String url = PicsMenu.getHomePage(menu, permissions);
		if (url == null)
			throw new Exception("No Permissions or Default Webpages found");

		getResponse().sendRedirect(url);
		return;
	}

	private void logAttempt() throws Exception {

		String remoteAddress = getRequest().getRemoteAddr();

		char successful = 'N';
		if (permissions.isLoggedIn()) {
			password = "*";
			successful = 'Y';
		}

		UserLoginLog loginLog = new UserLoginLog();
		loginLog.setLoginDate(new Date());
		loginLog.setUsername(username);
		loginLog.setPassword(password);
		loginLog.setRemoteAddress(remoteAddress);
		loginLog.setSuccessful(successful);
		if (permissions.getUserId() > 0)
			loginLog.setUserID(permissions.getUserId());
		if (permissions.getAdminID() > 0)
			loginLog.setAdmin(new User(permissions.getAdminID()));

		loginLogDAO.save(loginLog);
	}

	
	//////// GETTER & SETTERS ////////
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
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSwitchToUser(int switchToUser) {
		this.switchToUser = switchToUser;
	}
	
	

}
