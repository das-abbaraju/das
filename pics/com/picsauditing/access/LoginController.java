package com.picsauditing.access;

import java.sql.ResultSet;
import com.picsauditing.PICS.*;

public class LoginController extends DataBean{
	public String accountID = "";
	public String userID = "";
	public String accountType = "";
	public String accountName = "";
	public String name = "";

	public boolean checkLogin(String lname, String lpass, javax.servlet.http.HttpServletRequest request,
				com.picsauditing.PICS.PermissionsBean pBean) throws Exception {
		String name = "";
		lname = Utilities.escapeQuotes(lname);
		lpass = Utilities.escapeQuotes(lpass);
		String selectQuery = "SELECT accounts.name,accounts.type,accounts.id,accounts.active,"+
				"users.id,users.name FROM accounts LEFT JOIN users "+
				"ON accounts.id=users.accountID WHERE users.username='"+
				lname+"' AND users.password='"+lpass+"';";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()){
				accountName = SQLResult.getString("accounts.name");
				accountType = SQLResult.getString("accounts.type");
				accountID = SQLResult.getString("accounts.id");
				name = SQLResult.getString("users.name");
				userID = SQLResult.getString("users.id");

				if (!"Y".equals(SQLResult.getString("active"))) {
					errorMessages.addElement("That account is currently inactive.<br>Please contact PICS to activate your account.");
					SQLResult.close();
					logLoginAttempt(request,accountName,accountType,name,lname,"*","N",accountID,userID);
					DBClose();
					return false;
				}//if not active
				String updateQuery = "UPDATE accounts SET lastLogin=NOW() WHERE id="+accountID+" LIMIT 1;";
				SQLStatement.executeUpdate(updateQuery);
				updateQuery = "UPDATE users SET lastLogin=NOW() WHERE id="+userID+" LIMIT 1;";				
				SQLStatement.executeUpdate(updateQuery);
				SQLResult.close();

				logLoginAttempt(request,accountName,accountType,name,lname,"*","Y",accountID,userID);
				DBClose();
				pBean.setUserAccess(userID);
				return true;
			}//if
			errorMessages.addElement("Invalid username/password combination.");
			SQLResult.close();
			logLoginAttempt(request,"","","",lname,lpass,"N","","");
		}finally{
			DBClose();
		}//finally
		return false;
	}//checkLogin

	public void logLoginAttempt(javax.servlet.http.HttpServletRequest request,
				String accountName, String type, String name, String lname, String lpass, 
				String success, String accountID, String userID) throws Exception {
/*
		String insertQuery = "INSERT INTO loginLog (accountName,type,name,username,password,"+
			"successful,date,remoteAddress,id,userID) VALUES ('"+
			Utilities.escapeQuotes(name)+"','"+type+"','"+Utilities.escapeQuotes(name)+
			"','"+lname+"','"+lpass+"','"+success+"',NOW(),'"+request.getRemoteAddr()+
			"',"+Utilities.intToDB(accountID)+","+Utilities.intToDB(userID)+");";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
*/	}//logLoginAttempt
}//LoginController