package com.picsauditing.access;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
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
			
			if (adminID > 0) {
				// Re login the admin on logout
				user = userDAO.find(adminID);
				
				permissions.login(user);
				postLogin();
				return SUCCESS;
			}
			
			return SUCCESS;
		}
		
		if ("forgot".equals(button)) {
			sendPasswordReminder();
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
		
		// Login the user
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

			// /////////////////
			permissions.login(user);
			
			user.setLastLogin(new Date());
			user.getAccount().setLastLogin(new Date());
			userDAO.save(user);
			
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

	private boolean sendPasswordReminder() {
		if (!Utilities.isValidEmail(email)) {
			addActionError("Please enter a valid email address.");
			return false;
		}
		EmailBuilder emailBuilder = new EmailBuilder();

		List<User> matchingUsers = userDAO.findByEmail(email);
		if (matchingUsers.size() == 0) {
			addActionError("No account in our records has that email address.  Please verify it is "
					+ "the one you used when creating your PICS company profile.");
			return false;
		}
		try {
			for(User matchingUser : matchingUsers) {
				emailBuilder.setTemplate(24); // Password Reminder
				emailBuilder.setUser(matchingUser);
				EmailQueue emailQueue = emailBuilder.build();
				emailQueue.setPriority(100);
				
				EmailSender sender = new EmailSender();
				sender.sendNow(emailQueue);
				
				addActionMessage("An email has been sent to this address: <b>" + email + "</b> "
						+ "with your PICS account login information");
			}
			
			
		} catch (Exception e) {
			addActionError("Failed to send emails");
		}
		return true;
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

		if (user.getAccount().isOperator() || user.getAccount().isCorporate())
			if (!user.getAccount().isActiveB())
				return user.getAccount().getName() + " is no longer active.<br>Please contact PICS if you have any questions.";
		
		if (user.getIsActive() != YesNo.Yes)
			return "This account for " + user.getAccount().getName() + " is no longer active.<br>Please contact your administrator to reactivate it.";

		if (user.getLockUntil() != null && user.getLockUntil().after(new Date())) {
			return "This account is locked because of too many failed attempts. " +
					"You will be able to try again in " + DateBean.prettyDate(user.getLockUntil());
		}
		if (Strings.isEmpty(password)) {
			return "You must enter a password";
		}
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
			return "The password is not correct. You have " + (8 - user.getFailedAttempts()) 
				+ " attempts remaining before your account will be locked for one hour.";
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
		String url = null;
		if(permissions.isContractor() && !user.getAccount().isActiveB()) {
			ContractorAccount cAccount = (ContractorAccount) user.getAccount();
			if(cAccount.getRiskLevel() == null)
				url = "ContractorRegistrationServices.action?id=" + cAccount.getId();
			else if(cAccount.getOperators().size() == 0)
				url = "ContractorFacilities.action?id=" + cAccount.getId();
			else if(!cAccount.isPaymentMethodStatusValid())
				url = "ContractorPaymentOptions.action?id="+cAccount.getId();
			else
				url = "ContractorEdit.action?id="+cAccount.getId();
		}
		else 
			url = PicsMenu.getHomePage(menu, permissions);
		if (url == null)
			throw new Exception("No Permissions or Default Webpages found");
		
		getResponse().sendRedirect(url);
		return;
	}

	private void logAttempt() throws Exception {
		if (Strings.isEmpty(username))
			return;

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
		loginLog.setSessionId(getRequest().getSession().getId());
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
		this.username = username.trim();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSwitchToUser(int switchToUser) {
		this.switchToUser = switchToUser;
	}
	
	

}
