package com.picsauditing.access;


import java.sql.ResultSet;
import java.util.HashSet;

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
	
	public boolean loginByAdmin(String userID, javax.servlet.http.HttpServletRequest request) throws Exception {
		// Set the permissions from the session or create a new one if necessary
		Permissions permissions = (Permissions)request.getSession().getAttribute("permissions");
		if (permissions == null) {
			return false;
		}
		
		permissions.tryPermission(OpPerms.SwitchUser);
		
		int adminID = permissions.getUserId();

		User user = new User();
		user.setFromDB(userID);
		if (user.userDO.id != userID) return false;
		
		// Log the user in now
		permissions.login(user);
		permissions.setAdminID(adminID);
		
		// We should remove these next lines after we remove it from the application
		request.getSession().setAttribute("usertype", permissions.getAccountType());
		request.getSession().setAttribute("userid", permissions.getUserIdString());
		
		logAttempt(permissions, "", request);
		//user.updateLastLogin();
		return true;
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
		
		String insertQuery = "INSERT INTO loginlog SET " +
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
}
