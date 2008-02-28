package com.picsauditing.access;

import java.sql.*;
import java.util.Map;

import com.picsauditing.PICS.*;

public class UserDO {
	public String id = "";
	public String name = "";
	public String email = "";
	public String username = "";
	public String password = "";
	public String isActive = "Yes";
	public String dateCreated = "";
	public String lastLogin = "";
	public String accountID = "";
	public String isGroup = "No";
	public String accountType = "";
	public String accountName = "";
	
	public void setFromRequest(javax.servlet.http.HttpServletRequest request) throws Exception {
		try {
			if (request.getParameter("id") != null) id = request.getParameter("id");
			if (request.getParameter("name") != null) name = request.getParameter("name");
			if (request.getParameter("username") != null) username = request.getParameter("username");
			if (request.getParameter("email") != null) email = request.getParameter("email");
			String newPassword = request.getParameter("newPassword");
			if (!"".equals(newPassword))
				password = newPassword;
			if (request.getParameter("accountID") != null) accountID = request.getParameter("accountID");
			if (request.getParameter("isGroup") != null) isGroup = request.getParameter("isGroup");
			if (request.getParameter("isActive") != null) isActive = request.getParameter("isActive");
		} catch (Exception ex) {
			this.clear();
			throw ex;
		}
	}//setFromRequest

	private void clear() {
		id = "";
		name = "";
		email = "";
		username = "";
		password = "";
		dateCreated = "";
		lastLogin = "";
		accountID = "";
		accountType = "";
		accountName = "";
	}

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		try {
			id = SQLResult.getString("id");
			name = SQLResult.getString("name");
			username = SQLResult.getString("username");
			password = SQLResult.getString("password");
			isActive = SQLResult.getString("isActive");
			email = SQLResult.getString("email");
			dateCreated = DateBean.toShowFormat(SQLResult.getString("dateCreated"));
			lastLogin = DateBean.toShowFormat(SQLResult.getString("lastLogin"));
			accountID = SQLResult.getString("accountID");
			isGroup = SQLResult.getString("isGroup");
		} catch (Exception ex) {
			this.clear();
			throw ex;
		} finally {
			try{
				accountName = SQLResult.getString("account_name");
				accountType = SQLResult.getString("account_type");
			} catch (SQLException e) {
				
			}
		}
	}//setFromResultSet

	public String toString(){
		return "name="+name+","+
			"username="+username+","+
			"username="+username+","+
			"isActive="+isActive+","+
			"account="+accountName+","+
			"email="+email;
	}//toString
}