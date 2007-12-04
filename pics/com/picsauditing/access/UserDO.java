package com.picsauditing.access;

import java.sql.*;
import com.picsauditing.PICS.*;

public class UserDO{
	public String id = "";
	public String name = "";
	public String email = "";
	public String username = "";
	public String password = "";
	public String isActive = "Yes";
	public String dateCreated = "";
	public String lastLogin = "";
	public String accountID = "";

	public void setFromRequest(javax.servlet.http.HttpServletRequest request) throws Exception {
		name =request.getParameter("name");
		username = request.getParameter("username");
		email = request.getParameter("email");
		String newPassword = request.getParameter("newPassword");
		if (!"".equals(newPassword))
			password = newPassword;
		isActive = request.getParameter("isActive");	
	}//setFromRequest

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		id = SQLResult.getString("id");
		name = SQLResult.getString("name");
		username = SQLResult.getString("username");
		password = SQLResult.getString("password");
		isActive = SQLResult.getString("isActive");
		email = SQLResult.getString("email");
		dateCreated = DateBean.toShowFormat(SQLResult.getString("dateCreated"));
		lastLogin = DateBean.toShowFormat(SQLResult.getString("lastLogin"));
		accountID = SQLResult.getString("accountID");
	}//setFromResultSet

	public String toString(){
		return "name="+name+","+
			"username="+username+","+
			"password="+password+","+
			"isActive="+isActive+","+
			"accountID="+accountID+","+
			"email="+email;
	}//toString
}//UserBean