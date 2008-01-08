package com.picsauditing.access;


import com.picsauditing.PICS.DataBean;
import com.picsauditing.PICS.Utilities;

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
			logLoginAttempt(request,null, "",username,password,"N",0);
			getErrors().add(error);
			permissions.clear();
			return;
		}
		
		// We have a valid username and password and the user is active
		// Log the user in now
		permissions.login(user);
		request.getSession().setAttribute("usertype", permissions.getAccountType());
		request.getSession().setAttribute("userid", permissions.getUserIdString());
		
		logLoginAttempt(request, null, "", username, "*", "Y", permissions.getUserId());
		user.updateLastLogin();
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
			return "This user does not have permission to login";
		
		return "";
	}

	public void logout(javax.servlet.http.HttpSession session) {
		Permissions permissions = (Permissions)session.getAttribute("permissions");
		permissions.clear();
	}
	
	public void logLoginAttempt(javax.servlet.http.HttpServletRequest request, String accountName, String type, String lname, String lpass, 
			String success, int userID) throws Exception {
		String remoteAddress = null;
		if(request != null)
			remoteAddress = request.getRemoteAddr();
		
		String insertQuery = "INSERT INTO loginLog (company,type,username,password,"+ 
		"successful,date,remoteAddress,id) VALUES ('"+
			Utilities.escapeQuotes(accountName)+"','"+type+"','" +lname+"','"+lpass+"','"+success+"',NOW(),'"+ remoteAddress +
			"'," + userID +");";
		//System.out.println(insertQuery);	
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//logLoginAttempt
}//LoginController