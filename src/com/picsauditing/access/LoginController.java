package com.picsauditing.access;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.NoResultException;
import javax.servlet.http.Cookie;

import com.picsauditing.PICS.AccountBean;
import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DataBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PermissionsBean;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserLoginLogDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserLoginLog;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.SpringUtils;

/**
 * Populate the permissions object in session with appropriate login credentials
 * and access/permission data
 * 
 * @author Glenn & Trevor
 * 
 */
public class LoginController extends DataBean {
	private boolean isUser = true;
	private User user;
	private AccountBean aBean;
	private int loginByAdmin = 0;
	// private String prevLastLogin = "1/1/01";

	private Permissions permissions;
	private PermissionsBean pBean;
	protected UserDAO userDAO = (UserDAO) SpringUtils.getBean("UserDAO");
	protected UserLoginLogDAO loginLogDAO = (UserLoginLogDAO) SpringUtils.getBean("UserLoginLogDAO");;

	public boolean login(String username, String password, javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response) throws Exception {
		setupPerms(request);

		getErrors().clear();
		User user = userDAO.findName(username);
		if (user != null && DateBean.getDateDifference(user.getLockUntil(), new Date()) < 0) {
			return false;
		}
		String error = canLogin(username, password);
		int failedAttempts = 0;
		if (error.length() > 0) {
			failedAttempts++;
			if (user != null) {
				user.setFailedAttempts(failedAttempts);
				userDAO.save(user);
			}
			logAttempt(permissions, username, password, request);
			getErrors().add(error);
			return false;
		}

		// /////////////////
		this.doLogin(request.getSession(), true);
		logAttempt(permissions, username, password, request);
		postLogin(request, response);

		return true;
	}

	public boolean loginByAdmin(String userName, javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response) throws Exception {
		permissions = (Permissions) request.getSession().getAttribute("permissions");
		permissions.tryPermission(OpPerms.SwitchUser);
		this.loginByAdmin = this.permissions.getUserId();

		if (!getAccountByUsername(userName))
			return false;

		setupPerms(request);
		permissions.setAdminID(loginByAdmin);

		// /////////////////
		this.doLogin(request.getSession(), false);
		logAttempt(permissions, userName, "switch_user", request);
		postLogin(request, response);

		return true;
	}

	private void setupPerms(javax.servlet.http.HttpServletRequest request) {
		// Set the permissions from the session or create a new one if necessary
		permissions = (Permissions) request.getSession().getAttribute("permissions");
		if (permissions == null) {
			permissions = new Permissions();
			request.getSession().setAttribute("permissions", permissions);
		} else
			permissions.clear();

		// Also set the pBean for backwards compatibility...remove when we phase
		// out the pBean completely
		pBean = (PermissionsBean) request.getSession().getAttribute("pBean");
		if (pBean == null) {
			pBean = new PermissionsBean();
			request.getSession().setAttribute("pBean", pBean);
		}
		// Stuff the session permissions object into the legacy pBean
		pBean.setPermissions(permissions);
		// End of the pBean
	}

	/**
	 * Figure out if the current username/password is a valid user or account
	 * that can actually login. But don't actually login yet
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private String canLogin(String username, String password) throws Exception {
		if (username == null || username.equals(""))
			return "Enter a username";

		// We have 108 contractors with username length = 3, so we can't stop
		// them from logging in
		// But going forward we'll require 4 or more
		if (username.length() < 3)
			return "Enter a username with atleast 5 characters";

		if (!getAccountByUsername(username))
			return "No account exists with that username";

		if (isUser) {
			user = userDAO.findName(username);
			if (user != null) {
				if (!user.getPassword().equals(password))
					return "The password is not correct";

				if (user.getAccount().getActive() != 'Y')
					return "This user does not have permission to login.<br>Please contact PICS to activate your account.";

				if (user.getIsActive() != YesNo.Yes)
					return "This user does not have permission to login.<br>Please contact PICS to activate your account.";
			}
		} else {
			if (!aBean.password.equals(password))
				return "The password is not correct";

			if (!aBean.active.startsWith("Y"))
				return "This user does not have permission to login.<br>Please contact PICS to activate your account.";
		}

		// We are now ready to actually do the login (doLogin)
		return "";
	}

	private boolean getAccountByUsername(String username) throws Exception {
		Integer id = 0;
		aBean = new AccountBean();
		id = aBean.findID(username);
		if (id != 0) {
			aBean.setFromDB(id.toString());
			user = null;
			isUser = false;
		} else {
			try {
				user = userDAO.findName(username);
			} catch (NoResultException e) {
				return false;
			}
			if (user != null) {
				id = user.getId();
				aBean.setFromDB(user.getAccount().getIdString());
			} else
				return false;
		}
		// The user or account we want to login as is now set as private
		// variables
		return true;
	}

	private boolean getAccountByID(String id) throws Exception {
		aBean = new AccountBean();
		user = userDAO.find(Integer.parseInt(id));
		if (user != null) {
			aBean.setFromDB(user.getAccount().getIdString());
		} else {
			// Wait this could be a contractor trying to login, check the
			// accounts table
			aBean.setFromDB(id);
			if (!aBean.isSet())
				return false;
			user = null;
			isUser = false;
		}
		// The user or account we want to login as is now set as private
		// variables
		return true;
	}

	/**
	 * Perform the actual login process...store any info in the session that
	 * will be required in later pages
	 */
	private void doLogin(javax.servlet.http.HttpSession session, boolean updateLastLogin) throws Exception {
		if (isUser) {
			permissions.login(this.user);
			// this.prevLastLogin = this.user.getLastLogin().toString();
			if (updateLastLogin) {
				this.user.setLastLogin(new Date());
				userDAO.save(user);
				this.aBean.updateLastLogin();
			}

			// Most (if not all) of this below should eventually be phased out
			pBean.oBean = new com.picsauditing.PICS.OperatorBean();
			pBean.setUserName(permissions.getName());

			if (permissions.isOperator() || permissions.isCorporate()) {
				pBean.oBean.isCorporate = permissions.isCorporate();
				pBean.oBean.setFromDB(permissions.getAccountIdString());
				permissions.setCanSeeAudit(pBean.oBean.getCanSeeAuditIDSet());
				permissions.setApprovesRelationships(pBean.oBean.isApprovesRelationships());
			}
			// TODO we should allow each account to set their own timeouts
			// ie..session.setMaxInactiveInterval(user.getAccountTimeout());
			if (permissions.isPicsEmployee())
				session.setMaxInactiveInterval(3600);
		} else {
			// Contractors
			permissions.login(this.aBean);
			// this.prevLastLogin = this.aBean.lastLogin;
			if (updateLastLogin)
				this.aBean.updateLastLogin();

			// Most (if not all) of this below should eventually be phased out
			pBean.userID = permissions.getAccountIdString();
			pBean.setAllFacilitiesFromDB(permissions.getAccountIdString());
		}

		// Most (if not all) of this below should eventually be phased out
		pBean.loggedIn = true;
		pBean.setUserID(permissions.getAccountIdString());
		pBean.setUserName(permissions.getUsername());
		pBean.setUserType(permissions.getAccountType());
		pBean.setCanSeeSet(canSeeSet());
	}

	/**
	 * After we're logged in, now what should we do?
	 */
	private void postLogin(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response) throws Exception {
		if (!isUser) {
			ContractorBean cBean = new ContractorBean();
			cBean.setFromDB(permissions.getAccountIdString());

			// if ("".equals(cBean.accountDate)) {
			// cBean.accountDate = DateBean.getTodaysDate();
			// cBean.writeToDB();
			// //
			// response.sendRedirect("con_selectFacilities.jsp?id="+permissions.getAccountIdString());
			// return;
			// }
			// String loginStartDate = "1/1" + "/" +
			// String.valueOf(DateBean.getCurrentYear());
			// String loginStartDate =
			// this.getServletContext().getInitParameter("loginStartDate");
			// if(DateBean.isFirstBeforeSecond(prevLastLogin,loginStartDate)) {
			// response.sendRedirect("con_selectFacilities.jsp?id="+permissions.getAccountIdString());
			// return;
			// }
		}

		MenuComponent menu = PicsMenu.getMenu(permissions);

		// Find out if the user previously timed out on a page, we'll forward
		// back there below
		Cookie[] cookiesA = request.getCookies();
		if (cookiesA != null) {
			String fromURL = "";
			for (int i = 0; i < cookiesA.length; i++) {
				if ("from".equals(cookiesA[i].getName())) {
					fromURL = cookiesA[i].getValue();
					// Clear the cookie, now that we've used it once
					Cookie fromCookie = new Cookie("from", "");
					response.addCookie(fromCookie);
					if (fromURL.length() > 0) {
						response.sendRedirect(fromURL);
						return;
					}
				}
			}
		}

		String url = PicsMenu.getHomePage(menu, permissions);
		if (url == null)
			throw new Exception("No Permissions or Default Webpages found");

		response.sendRedirect(url);
		return;
	}

	public void logout(Permissions permissions, javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response) throws Exception {
		Integer adminID = permissions.getAdminID();
		permissions.clear();
		request.getSession().invalidate();
		if (adminID > 0) {
			setupPerms(request);
			if (!getAccountByID(adminID.toString()))
				;

			this.doLogin(request.getSession(), false);
			postLogin(request, response);
			return;
		}

		String temp = request.getParameter("msg");
		String query = "";
		if (null != temp && temp.length() > 0)
			query = "?msg=" + temp;
		response.sendRedirect("login.jsp" + query);
	}

	private void logAttempt(Permissions permissions, String username, String password,
			javax.servlet.http.HttpServletRequest request) throws Exception {

		String remoteAddress = "";
		if (request != null)
			remoteAddress = request.getRemoteAddr();

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

	/**
	 * @deprecated
	 */
	private HashSet<String> canSeeSet() throws Exception {
		try {
			String id = permissions.getAccountIdString();
			DBReady();
			HashSet<String> canSeeSet = new HashSet<String>();
			canSeeSet = new HashSet<String>();
			if (permissions.isContractor()) {
				canSeeSet.add(id);
				return canSeeSet;
			}
			if (permissions.isCorporate()) {
				id = "SELECT opID FROM facilities WHERE corporateID=" + id;
			}
			String selectQuery = "SELECT subID FROM accounts a JOIN generalcontractors gc ON a.id=subID "
					+ "WHERE a.active='Y' AND genID IN (" + id + ")";
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				canSeeSet.add(SQLResult.getString("subID"));
			SQLResult.close();

			return canSeeSet;
		} finally {
			DBClose();
		}
	}
}
