package com.picsauditing.access;


import com.picsauditing.PICS.DataBean;
import com.picsauditing.PICS.Utilities;


public class LoginController extends DataBean{
	
	public void login(String lname, String lpass, javax.servlet.http.HttpServletRequest request) throws Exception{
		if(lname == null || lname.equals("")) 
			return;
		
		//create new user
		User user = new User();
			
		try{
			
			checkUsername(user, lname);
			checkPassword(user, lpass);
			isActive(user);			
			logLoginAttempt(request, null, "", lname,"*","Y", Integer.parseInt(user.userDO.id));
			user.updateLastLogin();
			if(request != null)
				setPermission(user, request);
					
		}catch(Exception ex){
			logLoginAttempt(request,null, "",lname,lpass,"N",0);
			getErrors().add(ex.getMessage());
		}	
				
	}
	
	public void logout(javax.servlet.http.HttpSession session){
			Permissions permissions = getPermissions(session);
			
	}
		
	private void checkUsername(User user, String lname) throws Exception {
		try{
			user.usernameExists(lname);
		}catch(Exception ex){
			throw new Exception("The username, " + lname + ", does not exist");
		}
	}
	
	private void checkPassword(User user, String lpass) throws Exception {
		  if(!user.userDO.password.equals(lpass))
			throw new Exception("The password is incorrect");
	}
		
	private void isActive(User user) throws Exception{
		if(!user.userDO.isActive.equals("Yes"))
			throw new Exception("User, " + user.userDO.username + ", is no longer active");
	}
	
	public Permissions getPermissions(javax.servlet.http.HttpSession session){
		return (Permissions)session.getAttribute("permissions");
	}
	
	private void setPermission(User user, javax.servlet.http.HttpServletRequest request) throws Exception{
		Permissions permissions = new Permissions(user);		
		request.getSession().setAttribute("permissions", permissions);
		
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
		System.out.println(insertQuery);	
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//logLoginAttempt
	
	
		

}//LoginController