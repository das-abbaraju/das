package com.picsauditing.access;

import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;

import com.picsauditing.PICS.*;
import com.picsauditing.mail.EmailTemplates;
import com.picsauditing.mail.EmailUserBean;

public class User extends DataBean implements Comparable<User> {
	private static final int SU_GROUP = 9; // Group that automatically has ALL grant privileges
	public boolean isSuGroup() {
		return Integer.parseInt(this.userDO.id) == SU_GROUP;
	}
	
	//private static final int PASSWORD_DURATION = 365; // days between required password update
	private static final int MIN_PASSWORD_LENGTH = 5; // minimum required length of a password
	
	public UserDO userDO = new UserDO();
	String oldUsername = "";
	private Set<Permission> permissions;
	private Set<User> groups = new TreeSet<User>();
	
	public int hashCode() {
		return 100 + Integer.parseInt(userDO.id);
	}
	public boolean equals(Object obj){
		if(this == obj)
			return true;
		
		if((obj == null) || (obj.getClass() != this.getClass()))
				return false;
	
		User test = (User)obj;
		return (userDO.id.equals(test.userDO.id));
	}
	
	public void setFromDB(String uID) throws Exception {
		if (uID == null) return;
		int temp = Integer.parseInt(uID);
		if (temp < 1) return;
		
		String query = "SELECT users.*, a.name account_name, a.type account_type " +
				"FROM users LEFT JOIN accounts a ON users.accountID = a.id " +
				"WHERE users.id = "+temp;
		selectFromDB(query);
	}//setFromDB
	
	/**
	 * Grabs only the first user attached to this account like a contractor
	 * Don't use this if the account type isn't a contractor
	 * @param accountID
	 * @throws Exception
	 */
	public void setFromAccountID(String accountID) throws Exception {
		String query = "SELECT * FROM users WHERE accountID="+accountID +" LIMIT 1";
		selectFromDB(query);
	}//setFromDB
	
	public int findID(String username) throws SQLException {
		int id = 0;
		try {
			DBReady();
			String sql = "SELECT id FROM users WHERE username='"+Utilities.escapeQuotes(username)+"';";
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			if (SQLResult.next())
				id = SQLResult.getInt("id");
			SQLResult.close();
		}finally{
			DBClose();
		}
		return id;
	}
	
	public boolean usernameExists(String username, int currentID) throws SQLException {
		int id = this.findID(username);
		if (id == 0) {
			AccountBean aBean = new AccountBean();
			id = aBean.findID(username);
		}
		if (id==currentID) return false;
		return (id==0);
	}
	public boolean usernameExists(String username) throws SQLException {
		return this.usernameExists(username, -1);
	}
	
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
		this.isSet = true; // we may want isSet to just mean we have a row in the database
	}
		
	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		userDO.setFromResultSet(SQLResult);
		this.isSet = true;
	}
	
	public void writeToDB() throws Exception {
		StringBuilder sql = new StringBuilder();

		int userID = Integer.parseInt(Utilities.intToDB(userDO.id));
		if (userID == 0) {
			sql.append("INSERT");
		} else {
			sql.append("UPDATE");
		}
		sql.append(" users SET ");
		sql.append(" name='").append(Utilities.escapeQuotes(userDO.name)).append("'");
		sql.append(", isActive='").append(Utilities.escapeQuotes(userDO.isActive)).append("'");
		sql.append(", accountID='").append(Utilities.intToDB(userDO.accountID)).append("'");
		sql.append(", isGroup='").append(Utilities.escapeQuotes(userDO.isGroup)).append("'");
		
		if (userDO.isGroup.equals("No")) {
			sql.append(", username='").append(Utilities.escapeQuotes(userDO.username)).append("'");
			sql.append(", password='").append(Utilities.escapeQuotes(userDO.password)).append("'");
			sql.append(", email='").append(Utilities.escapeQuotes(userDO.email)).append("'");
		} else {
			// Every user/group must have a username, so just make one up
			// We may need to try harder to make this unique like add in the accountID as well
			sql.append(", username='GROUP").append(Utilities.escapeQuotes(userDO.name)).append("'");
		}
		
		if (userID > 0) {
			sql.append(" WHERE id=").append(userID);
		} else {
			sql.append(", dateCreated=NOW()");
		}
		try{
			DBReady();
			SQLStatement.executeUpdate(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			if (userID == 0) {
				ResultSet SQLResult = SQLStatement.getGeneratedKeys();
				if (SQLResult.next())
					userDO.id = SQLResult.getString("GENERATED_KEY");
				SQLResult.close();
			}
		}finally{
			DBClose();
		}
	}
	
	public void writeNewToDB(String accountID, HttpServletRequest request) throws Exception {
		userDO.accountID = accountID;
		String insertQuery = "INSERT INTO users (name, username, password, email, isActive, dateCreated, accountID) VALUES ('"+
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
		}finally{
			DBClose();
		}//finally
	}//addUser
	
	public void updateLastLogin() throws Exception {
		
		String updateQuery = "UPDATE users SET lastLogin=NOW() WHERE id="+userDO.id+" LIMIT 1;";		
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}
	}

	public void deleteUser() throws Exception {
		if (!this.isSet) return;
		
		try{
			DBReady();
			// With MyISAM we Auto commit anyways
			String sql;
			SQLStatement.getConnection().setAutoCommit(false);
			
			sql = "DELETE FROM usergroup WHERE groupID="+userDO.id;
			SQLStatement.executeUpdate(sql);

			sql = "DELETE FROM usergroup WHERE userID="+userDO.id;
			SQLStatement.executeUpdate(sql);

			sql = "DELETE FROM useraccess WHERE userID="+userDO.id;
			SQLStatement.executeUpdate(sql);
			
			sql = "INSERT INTO deletedUsers "+
				"(id,name,email,accountID,dateCreated,dateDeleted) " +
				"SELECT id,name,email,accountID,dateCreated,NOW() " +
				"FROM users WHERE id="+userDO.id;
			SQLStatement.executeUpdate(sql);
			
			sql = "DELETE FROM users WHERE id="+userDO.id;
			SQLStatement.executeUpdate(sql);
			
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
		}finally{
			DBClose();
		}//finally
	}
	
	public boolean sendPasswordEmail(String email) throws Exception {
		if (!Utilities.isValidEmail(email)) {
			errorMessages.addElement("Please enter a valid email address.");
			return false;
		}
		SQLBuilder sql = new SQLBuilder();
		sql.setFromTable("users");
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
			}
			
			String userID = SQLResult.getString("id");
			EmailUserBean mailer = new EmailUserBean();
			mailer.sendMessage(EmailTemplates.password, userID, new Permissions());
			errorMessages.addElement("An email has been sent to: <b>" + email + "</b> with your PICS login information");
			SQLResult.close();
			DBClose();
			return true;
		}finally{
			DBClose();
		}
	}

	public boolean isOK() throws Exception {
		errorMessages = new Vector<String>();
		if (userDO.name.length()==0)
			errorMessages.addElement("Please enter a name");
		else if (userDO.name.length() < 3)
			errorMessages.addElement("Please enter a name with more than 2 characters");
		
		if (userDO.isGroup.equals("Yes")) return (errorMessages.size() == 0);
		
		if (userDO.username.length() < 5)
			errorMessages.addElement("Please choose a username at least 5 characters long");
		if (userDO.password.length() < MIN_PASSWORD_LENGTH)
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
		if (userDO.password.equalsIgnoreCase(userDO.username))
			errorMessages.addElement("Please choose a password different from your username.");
		
		if (userDO.email.length() == 0 || !Utilities.isValidEmail(userDO.email))
			errorMessages.addElement("Please enter a valid email address.");

		return (errorMessages.size() == 0);
	}//isOK

	public Set<Permission> getPermissions() throws Exception{
		if(permissions != null)
			return permissions;
		
		// Our permissions are empty, so go get some
		permissions = new TreeSet<Permission>();
		
		if (isSuGroup()) {
			// This is the Super User Group, which should have grant ability on ALL permissions
			// Also grant view/edit/delete on EditUsers
			// SuperUser group does not inherit from parent groups
			for(OpPerms accessType : OpPerms.values()) {
				Permission perm = new Permission();
				perm.setAccessType(accessType);
				if (accessType.equals(OpPerms.EditUsers)) {
					perm.setViewFlag(true);
					perm.setEditFlag(true);
					perm.setDeleteFlag(true);
				} else {
					perm.setViewFlag(false);
					perm.setEditFlag(false);
					perm.setDeleteFlag(false);
				}
				perm.setGrantFlag(true);
				permissions.add(perm);
			}
			return permissions;
		}
		
		Set<Permission> tempPerms ;
		getGroups(); // get all the groups this user (or group) is a part of
		for(User group : groups){
			tempPerms = group.getPermissions();	
			for(Permission perm : tempPerms){
				this.add(permissions, perm);
			}
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
				if (perm.getAccessType() != null)
					this.add(permissions, perm);
			}
		}finally{
			SQLResult.close();
			DBClose();
		}//finally
		return permissions;
	}
	
	private void add(Set<Permission> permissions, Permission perm) {
		if (!permissions.contains(perm)) {
			// add the parent group's permissions to the user's permissions
			if (perm.getAccessType() == null) return;
			permissions.add(perm);
			return;
		}
		
		// Optimistic Granting
		// if the user has two groups with the same perm type, 
		// and one grants but the other revokes, then the users WILL be granted the right
		for(Permission origPerm : permissions) {
			if (origPerm.equals(perm)) {
				if (perm.isViewFlag()) origPerm.setViewFlag(true);
				if (perm.isEditFlag()) origPerm.setEditFlag(true);
				if (perm.isDeleteFlag()) origPerm.setDeleteFlag(true);
				if (perm.isGrantFlag()) origPerm.setGrantFlag(true);
			}
		}
	}
	
	/**
	 * Only return the permissions assigned directly to this User
	 * @return
	 * @throws Exception
	 */
	public Set<Permission> getOwnedPermissions() throws Exception {
		Set<Permission> ownPermissions = new TreeSet<Permission>();
		
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
				if (perm.getAccessType() != null)
					this.add(ownPermissions, perm);
			}
		}finally{
			SQLResult.close();
			DBClose();
		}//finally
		return ownPermissions;
	}

	public Set<User> getGroups() throws Exception{
		if(groups.size() > 0)
			return groups;
		
		if (!this.isSet) throw new IllegalStateException("userDO is not set");
		
		ResultSet SQLResult = null;
		String query = "select * from users u where id in (select groupID from usergroup where userID=" + userDO.id +") order by u.name";
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(query);
			while(SQLResult.next()){
				User temp = new User();
				temp.userDO.setFromResultSet(SQLResult);
				temp.isSet = true;
				groups.add(temp);
			}
			return groups;
		}finally{
			SQLResult.close();
			DBClose();
		}//finally
	}

	public Set<User> getAccountGroups() throws Exception {
		if (!this.isSet) throw new IllegalStateException("userDO is not set");
		
		Set<User> accountGroups = this.getGroups();
		ResultSet SQLResult = null;
		String query = "select * from users u where isGroup='Yes' and accountID = '" + Utilities.intToDB(userDO.accountID) +"' order by u.name";
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(query);
			while(SQLResult.next()){
				User temp = new User();
				temp.userDO.setFromResultSet(SQLResult);
				temp.isSet = true;
				accountGroups.add(temp);
			}
			return accountGroups;
		}finally{
			SQLResult.close();
			DBClose();
		}
	}

	public Set<User> getMembers() throws Exception {
		Set<User> members = new TreeSet<User>();
		
		ResultSet SQLResult = null;
		String query = "SELECT users.*, a.name account_name, a.type account_type " +
			"FROM users LEFT JOIN accounts a ON users.accountID = a.id " +
			"WHERE users.accountID = a.id AND users.id in (SELECT userID FROM usergroup WHERE groupID=" + userDO.id +") " +
			"ORDER BY users.isActive, users.name";
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
		}
		return members;
	}
	
	/**
	 * Add this user to a group, if it already exists, then just ignore the error
	 * @param groupID
	 * @throws Exception
	 */
	public void addToGroup(String groupID, Permissions byUser) throws Exception {
		if (this.userDO.id.equals(groupID)) return;
		String sql = "INSERT IGNORE INTO usergroup (userID, groupID, creationDate, createdBy)" +
				"VALUES (" + userDO.id + ", " + groupID +
				", NOW()," + byUser.getUserId() + ")";
		try{
			DBReady();
			SQLStatement.executeUpdate(sql);
		}finally{
			DBClose();
		}
	}
	/**
	 * Remove this user from the group identified by groupID
	 * 
	 * @param groupID
	 * @throws Exception
	 */
	public void removeFromGroup(String groupID) throws Exception {
		String sql = "DELETE FROM usergroup WHERE userID = " + userDO.id + " AND groupID = " + groupID;
		try{
			DBReady();
			SQLStatement.executeUpdate(sql);
		}finally{
			DBClose();
		}
	}
	
	@Override
	public int compareTo(User o) {
		if (!this.userDO.isActive.equals(o.userDO.isActive)) {
			// Sort Active before Inactive
			if (this.userDO.isActive.equals("Yes")) return -1;
			else return 1;
		}
		if (!this.userDO.isGroup.equals(o.userDO.isGroup)) {
			// Sort Groups before Users
			if (this.userDO.isGroup.equals("Yes")) return -1;
			else return 1;
		}
		// Then sort by name
		return this.userDO.name.compareToIgnoreCase(o.userDO.name);
	}
	
	/**
	 * 
	 * @param limit
	 * @return Columns: successful, date, remoteAddress, name
	 * @throws Exception
	 */
	public List<BasicDynaBean> getLoginLog(int limit) throws SQLException {
		SQLBuilder sql = new SQLBuilder("loginlog l");
		sql.addField("l.successful");
		sql.addField("l.date");
		sql.addField("l.remoteAddress");
		sql.addField("a.name");
		sql.addJoin("left join users a on l.adminID = a.id");
		sql.addWhere("l.username = '"+this.userDO.username+"'");
		sql.addOrderBy("l.date desc");
		sql.setLimit(limit);
		
		return executeQuery(sql);
	}
}