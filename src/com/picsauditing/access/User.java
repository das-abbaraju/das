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
	private Set<Permission> permissions;
	private List<User> groups = new ArrayList<User>();
	
	
	public void setFromDB(String uID) throws Exception {
		if (uID == null) return;
		Integer temp = Integer.parseInt(uID);
		if (temp < 1) return;
		
		String query = "SELECT * FROM users WHERE id="+uID+";";
		selectFromDB(query);
	}//setFromDB
	
	/**
	 * Grabs only the first user attached to this account like a contractor
	 * @param accountID
	 * @throws Exception
	 */
	public void setFromAccountID(String accountID) throws Exception {
		String query = "SELECT * FROM users WHERE accountID="+accountID +" LIMIT 1";
		selectFromDB(query);
	}//setFromDB
	
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
		/*
		String query = "SELECT u.*, a.type FROM users u LEFT JOIN accounts a ON u.accountID = a.id WHERE u.username='"+Utilities.escapeQuotes(u_name)+"'";
		selectFromDB(query);
		if (userDO.id.length() > 0) this.isSet = true;
		
		return this.isSet;
	*/
	
	}//usernameExists
	
	private void selectFromDB(String selectQuery) throws Exception{		
		ResultSet SQLResult = null;
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				setFromResultSet(SQLResult);			
		}finally{
			SQLResult.close();
			DBClose();
		}
	}

	public void setFromRequest(HttpServletRequest request) throws Exception {
		oldUsername = userDO.username;
		userDO.setFromRequest(request);
		this.isSet = true;
	}
		
	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		userDO.setFromResultSet(SQLResult);
		this.isSet = true;
	}
	
	
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
	
	public void updateLastLogin() throws Exception {
		
		String updateQuery = "UPDATE users SET lastLogin=NOW() WHERE id="+userDO.id+" LIMIT 1;";		
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}
	}

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
	
	
	public boolean sendPasswordEmail(String email) throws Exception {
		if (!Utilities.isValidEmail(email)) {
			errorMessages.addElement("Please enter a valid email address.");
			return false;
		}
		SQLBuilder sql = new SQLBuilder();
		sql.setFromTable("users");
		sql.addField("accountID");
		sql.addField("name");
		sql.addField("username");
		sql.addField("password");
		sql.addWhere("email='"+email+"'");
		
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql.toString());
			if (!SQLResult.next()) {
				errorMessages.addElement("No user account in our records has that email address. " +
					"Please verify it is the one you used when creating your PICS profile.");
				SQLResult.close();
				DBClose();
				return false;
			}//if
			
			EmailBean.sendPasswordEmail(SQLResult.getString("accountID"),SQLResult.getString("username"),SQLResult.getString("password"),email,SQLResult.getString("name"));
			errorMessages.addElement("An email has been sent to: <b>" + email + "</b> with your " + 
				"PICS login information");
			SQLResult.close();
			DBClose();
			return true;
		}finally{
			DBClose();
		}
	}	

	public boolean isOK() throws Exception {
		errorMessages = new Vector<String>();
		if (userDO.username.length() < 5)
			errorMessages.addElement("Please choose a username at least 5 characters long");
		if (userDO.password.length() < MIN_PASSWORD_LENGTH)
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
		if (userDO.password.equalsIgnoreCase(userDO.username))
			errorMessages.addElement("Please choose a password different from your username.");
		//if (!userDO.username.equals(oldUsername))
		//	errorMessages.addElement("That username already exists.<br>Please choose a different one.");
		
		if (userDO.email.length() == 0 || !Utilities.isValidEmail(userDO.email))
			errorMessages.addElement("Please enter a valid email address.");

		if (userDO.isActive == null)
			errorMessages.addElement("Please select whether this user is active or not");
		return (errorMessages.size() == 0);
	}//isOK

	public Set<Permission> getPermissions() throws Exception{
		if(permissions != null)
			return permissions;
		
		// Our permissions are empty, so go get some
		permissions = new HashSet<Permission>();
		
		try{
			Set<Permission> tempPerms ;
			getGroups(); // get all the groups this user (or group) is a part of
			for(User group : groups){
				tempPerms = group.getPermissions();	
				for(Permission perm : tempPerms){
					// add the parent group's permissions to the user's permissions
					// if the user has two groups with the same perm type, 
					// the last one will win
					permissions.add(perm);
				}
			}
		}catch(Exception ex){
			// Eat the error...may not be good
		}
		
		// READ the permissions assigned directly to this THIS user/group
		ResultSet SQLResult = null;
		String sql = "SELECT accessType, viewFlag, editFlag, deleteFlag, grantFlag " + 
			"FROM useraccess where userID=" + userDO.id;
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(sql);
			while(SQLResult.next()){
				Permission perm = new Permission();
				perm.setFromResultSet(SQLResult);
				permissions.add(perm);
			}
		}finally{
			SQLResult.close();
			DBClose();
		}//finally
		return permissions;
	}
	
	/**
	 * Only return the permissions assigned directly to this User
	 * @return
	 * @throws Exception
	 */
	public Set<Permission> getOwnedPermissions() throws Exception {
		Set<Permission> ownPermissions = new HashSet<Permission>();
		
		// READ the permissions assigned directly to this THIS user/group
		ResultSet SQLResult = null;
		String sql = "SELECT accessType, viewFlag, editFlag, deleteFlag, grantFlag " + 
			"FROM useraccess where userID=" + userDO.id;
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(sql);
			while(SQLResult.next()){
				Permission perm = new Permission();
				perm.setFromResultSet(SQLResult);
				ownPermissions.add(perm);
			}
		}finally{
			SQLResult.close();
			DBClose();
		}//finally
		return ownPermissions;
	}

	public List<User> getGroups() throws Exception{
		if(groups.size() > 0)
			return groups;
		
		ResultSet SQLResult = null;
		String query = "select u.*, null as type from users u where id in (select groupID from usergroup where userID=" + userDO.id +") order by u.name";
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(query);
			while(SQLResult.next()){
				User temp = new User();
				temp.userDO.setFromResultSet(SQLResult);
				groups.add(temp);
			}
			
			return groups;
			
		}finally{
			SQLResult.close();
			DBClose();
		}//finally
	}

	public List<User> getMembers() throws Exception {
		List<User> members = new ArrayList<User>();
		
		ResultSet SQLResult = null;
		String query = "select u.*, null as type from users u where id in (select userID from usergroup where groupID=" + userDO.id +") order by u.isActive, u.name";
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(query);
			while(SQLResult.next()){
				User temp = new User();
				temp.userDO.setFromResultSet(SQLResult);
				members.add(temp);
			}
		}finally{
			SQLResult.close();
			DBClose();
		}//finally
		return members;
	}
		
}//UserBean