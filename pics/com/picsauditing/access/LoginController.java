package com.picsauditing.access;

import java.sql.ResultSet;
import java.util.HashSet;

import com.picsauditing.PICS.*;

public class LoginController extends DataBean{
	
	public void login(String lname, String lpass, javax.servlet.http.HttpServletRequest request) throws Exception{
		//create new user
		User user = new User();
		try{
			checkUsername(user, lname, request);
			isActive(user);
			setPermission(user, request);				
					
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}	
				
	}
		
	private void checkUsername(User user, String lname,javax.servlet.http.HttpServletRequest request) throws Exception {
		if(!user.usernameExists(lname))
			throw new Exception("The username, " + lname + ", does not exist");
	}
		
	private void isActive(User user) throws Exception{
		if(user.userDO.isActive != "Yes")
			throw new Exception("User, " + user.userDO.username + ", is no longer active");
	}
	
	private void setPermission(User user, javax.servlet.http.HttpServletRequest request) {
		Permissions permissions = new Permissions(user.userDO);
		request.getSession().setAttribute("permissions", permissions);
		
	}
	
	/*
	public boolean checkLogin(String lname, String lpass, javax.servlet.http.HttpServletRequest request) throws Exception {
				
		lname = Utilities.escapeQuotes(lname);
		lpass = Utilities.escapeQuotes(lpass);
		int userid = 0;
		String selectQuery = "SELECT * from users where username='"+ lname+"' AND password='"+lpass+"';";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()){
				lname = SQLResult.getString("username");
				lpass = SQLResult.getString("password");
				userid = SQLResult.getInt("id");
				if (!"Y".equals(SQLResult.getString("active"))) {
					errorMessages.addElement("That account is currently inactive.<br>Please contact PICS to activate your account.");
					SQLResult.close();
					logLoginAttempt( request, null, null, lname, lpass, "N", userid );
					DBClose();
					return false;
				}//if not active
				String updateQuery = "UPDATE users SET lastLogin=NOW() WHERE id="+userid+" LIMIT 1;";				
				SQLStatement.executeUpdate(updateQuery);
				SQLResult.close();
				logLoginAttempt(request, null, null, lname,"*","Y",userid);
				DBClose();
				   
				return true;
			}//if
			errorMessages.addElement("Invalid username/password combination.");
			SQLResult.close();
			logLoginAttempt(request,null, null,lname,lpass,"N",0);
		}finally{
			DBClose();
		}//finally
		return false;
	}//checkLogin

	public void logLoginAttempt(javax.servlet.http.HttpServletRequest request, String accountName, String type, String lname, String lpass, 
				String success, int userID) throws Exception {
		
		String insertQuery = "INSERT INTO loginLog (accountName,type,name,username,password,"+
			"successful,date,userID) VALUES ('"+
			Utilities.escapeQuotes(accountName)+"','"+type+"','" +lname+"','"+lpass+"','"+success+"',NOW(),'"+request.getRemoteAddr()+
			"," + userID +");";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//logLoginAttempt
*/		
	
}//LoginController