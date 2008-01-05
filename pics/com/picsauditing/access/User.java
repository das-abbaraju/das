package com.picsauditing.access;

import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import com.picsauditing.PICS.*;

public class User extends DataBean {
	private static final int PASSWORD_DURATION = 365; // days between required password update
	private static final int MIN_PASSWORD_LENGTH = 5; // minimum required length of a passord
	
	public UserDO userDO = new UserDO();
	String oldUsername = "";
	public void setFromDB(String uID) throws Exception {
		userDO.id = uID;
		setFromDB();
	}//setFromDB

	public void setFromDB() throws Exception {
		String selectQuery = "SELECT * FROM users WHERE id="+userDO.id+";";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			else
				throw new Exception("No user with id: "+userDO.id);
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

	public void setFromRequest(HttpServletRequest request) throws Exception {
		oldUsername = userDO.username;
		userDO.setFromRequest(request);
	}//setFromRequest

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		userDO.setFromResultSet(SQLResult);
	}//setFromResultSet

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE users SET name='"+Utilities.escapeQuotes(userDO.name)+
			"',username='"+Utilities.escapeQuotes(userDO.username)+
			"',password='"+Utilities.escapeQuotes(userDO.password)+
			"',email='"+Utilities.escapeQuotes(userDO.email)+
			"',isActive='"+userDO.isActive+
			"' WHERE id="+userDO.id+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void writeNewToDB(String accountID, HttpServletRequest request) throws Exception {
		userDO.accountID = accountID;
		String insertQuery = "INSERT INTO users (name,username,password,email,isActive,dateCreated,accountID) VALUES ('"+
			Utilities.escapeQuotes(userDO.name)+"','"+
			Utilities.escapeQuotes(userDO.username)+"','"+
			Utilities.escapeQuotes(userDO.password)+"','"+
			Utilities.escapeQuotes(userDO.email)+"','"+
			userDO.isActive+"',"+
			"NOW(),"+
			accountID+");";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ResultSet SQLResult = SQLStatement.getGeneratedKeys();
			if (SQLResult.next())
				userDO.id = SQLResult.getString("GENERATED_KEY");
			SQLResult.close();
			EmailBean eBean = new EmailBean();
			eBean.sendNewUserEmail(request,accountID,userDO.name,userDO.username,userDO.password,userDO.email);
		}finally{
			DBClose();
		}//finally
	}//addUser

	public void deleteUser(String deleteID) throws Exception {
		setFromDB(deleteID);
		String insertQuery = "INSERT INTO deletedUsers(id,name,email," +
				"accountID,dateCreated,dateDeleted) VALUES("+
				deleteID+",'"+Utilities.escapeQuotes(userDO.name)+"','"+
				Utilities.escapeQuotes(userDO.email)+"',"+
				userDO.accountID+",'"+DateBean.toDBFormat(userDO.dateCreated)+"',NOW())";
		String deleteQuery = "DELETE FROM users WHERE id="+deleteID+" LIMIT 1";
		try{
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			SQLStatement.executeUpdate(insertQuery);
			SQLStatement.executeUpdate(deleteQuery);
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
		}finally{
			DBClose();
		}//finally
	}//deleteUser

	public boolean usernameExists(String u_name) throws Exception {
		boolean temp = false;
		String selectQuery = "SELECT id FROM accounts WHERE username='"+Utilities.escapeQuotes(u_name)+"';";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				temp = true;
			SQLResult.close();
			selectQuery = "SELECT id FROM users WHERE username='"+Utilities.escapeQuotes(u_name)+"';";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				temp = true;
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		return temp;
	}//usernameExists

	public boolean isOK() throws Exception {
		errorMessages = new Vector<String>();
		if (userDO.username.length() < 5)
			errorMessages.addElement("Please choose a username at least 5 characters long");
		if (userDO.password.length() < MIN_PASSWORD_LENGTH)
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
		if (userDO.password.equalsIgnoreCase(userDO.username))
			errorMessages.addElement("Please choose a password different from your username.");
		if (!userDO.username.equals(oldUsername) && usernameExists(userDO.username))
			errorMessages.addElement("That username already exists.<br>Please choose a different one.");
		
		if (userDO.email.length() == 0 || !Utilities.isValidEmail(userDO.email))
			errorMessages.addElement("Please enter a valid email address.");

		if (userDO.isActive == null)
			errorMessages.addElement("Please select whether this user is active or not");
		return (errorMessages.size() == 0);
	}//isOK
}//UserBean