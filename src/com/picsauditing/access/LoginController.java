package com.picsauditing.access;


import java.sql.ResultSet;

import com.picsauditing.PICS.DataBean;

/**
 * Populate the permissions object in session with appropriate login credentials and access/permission data
 * @author Glenn & Trevor
 *
 */
public class LoginController extends DataBean {
	
	public void login(String username, String password, javax.servlet.http.HttpServletRequest request) throws Exception {
		// Set the permissions from the session or create a new one if necessary
		Permissions permissions = (Permissions)request.getSession().getAttribute("permissions");
		if (permissions == null) {
			permissions = new Permissions();
			request.getSession().setAttribute("permissions", permissions);
		}

		User user = new User();
		getErrors().clear();
		String error = canLogin(user, username, password);
		if (error.length() > 0) {
			logAttempt(permissions, password, request);
			getErrors().add(error);
			permissions.clear();
			return;
		}
		
		// We have a valid username and password and the user is active
		// Log the user in now
		permissions.login(user);
		request.getSession().setAttribute("usertype", permissions.getAccountType());
		request.getSession().setAttribute("userid", permissions.getUserIdString());
		
		logAttempt(permissions, "", request);
		user.updateLastLogin();
	}
	
	public void loginByAdmin(String userID, javax.servlet.http.HttpServletRequest request) throws Exception {
		// Set the permissions from the session or create a new one if necessary
		Permissions permissions = (Permissions)request.getSession().getAttribute("permissions");
		if (permissions == null) {
			return;
		}
		
		if (!permissions.hasPermission(OpPerms.SwitchUser))
			return;
		
		int adminID = permissions.getUserId();

		User user = new User();
		user.setFromDB(userID);
		if (user.userDO.id != userID) return;
		
		// Log the user in now
		permissions.login(user);
		permissions.setAdminID(adminID);
		request.getSession().setAttribute("usertype", permissions.getAccountType());
		request.getSession().setAttribute("userid", permissions.getUserIdString());
		
		logAttempt(permissions, "", request);
		//user.updateLastLogin();
	}
	
	private String canLogin(User user, String username, String password) throws Exception {
		if(username == null || username.equals(""))
			return "Enter a username";

		if(username.length() < 4)
			return "Enter a username with atleast 4 characters";
		
		if (!user.usernameExists(username))
			return username + "is not a valid username";
		
		if(!user.userDO.password.equals(password))
			return "The password is not correct";
		
		if(!user.userDO.isActive.equals("Yes"))
			return "This user does not have permission to login.<br>Please contact PICS to activate your account.";
		
		return "";
	}

	public void logout(javax.servlet.http.HttpSession session) {
		Permissions permissions = (Permissions)session.getAttribute("permissions");
		permissions.clear();
	}
	
	private void logAttempt(Permissions permissions, String password, javax.servlet.http.HttpServletRequest request) throws Exception {
		String remoteAddress = "";
		if(request != null)
			remoteAddress = request.getRemoteAddr();
		
		String strSuccess = "N";
		if (permissions.isLoggedIn()) {
			password = "*";
			strSuccess = "Y";
		}
		
		String insertQuery = "INSERT INTO loginLog SET " +
				"username = '"+permissions.getUsername()+"', " + 
				"password = '"+password+"', " + 
				"successful = '"+strSuccess+"', " +
				"date = NOW(), " +
				"remoteAddress = '"+remoteAddress+"', " +
				"id = '"+permissions.getUserIdString()+"', " +
				"adminID = '"+permissions.getAdminID()+ "'";
		//System.out.println(insertQuery);	
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}
	}
	
	/*
	public boolean checkLogin(String lname, String lpass, javax.servlet.http.HttpServletRequest req) throws Exception {
		String selectQuery;
		ResultSet SQLResult;
		// set these for isFirstLogin(), and mustSubmitPQF()
		selectQuery = "SELECT accountDate, canEditPrequal FROM contractor_info WHERE id="+id+";";
		SQLResult = SQLStatement.executeQuery(selectQuery);
		if (SQLResult.next()){
			accountDate = DateBean.toShowFormat(SQLResult.getString("accountDate"));
			canEditPrequal = SQLResult.getString("canEditPrequal");
		}//if
		SQLResult.close();
		
		// Set canSeeSet
		canSeeSet = new HashSet<String>();
		if ("Contractor".equals(type))
			canSeeSet.add(id);
		selectQuery = "SELECT subID FROM accounts INNER JOIN generalcontractors ON (id=subID) "+
		"WHERE active='Y' AND genID="+id+";";
		SQLResult = SQLStatement.executeQuery(selectQuery);
		while (SQLResult.next())
			canSeeSet.add(SQLResult.getString("subID"));
		SQLResult.close();
		
		//Set auditorCanSeeSet BJ 10-28-04
		auditorCanSeeSet = new HashSet<String>();
		auditorCanSeeSet.add(id);
		selectQuery = "SELECT id FROM contractor_info WHERE auditor_id="+id+" OR desktopAuditor_id="+id+" OR pqfAuditor_id="+id+";";
		SQLResult = SQLStatement.executeQuery(selectQuery);
		while (SQLResult.next())
			auditorCanSeeSet.add(SQLResult.getString("id"));
		SQLResult.close();
		
		// Set hasCertSet
		hasCertSet = new HashSet<String>();
		selectQuery = "SELECT contractor_id FROM certificates WHERE operator_id="+id+";";
		SQLResult = SQLStatement.executeQuery(selectQuery);
		while (SQLResult.next())
			hasCertSet.add(SQLResult.getString("contractor_id"));
		SQLResult.close();
	}//checkLogin
	*/
}