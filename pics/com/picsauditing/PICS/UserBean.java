package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

import com.picsauditing.PICS.*;

public class UserBean extends DataBean {
/*	History
	5/22/06 jj - moved from AccountBean to here, own data bean
*/
	public String id = "";
	public String name = "";
	public String username = "";
	public String password = "";
	public String email = "";
	public String dateCreated = "";
	public String lastLogin = "";
	public String seeOsha = "";
	public String seeFullPQF = "";
	public String editFlagCriteria = "";
	public String editForcedFlags = "";
	public String editNotes = "";
	public String accountID = "";

	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;

	public boolean canSeeFullPQF(){
		return "Yes".equals(seeFullPQF);
	}//seeFullPQF
	public boolean canEditFlagCriteria(){
		return "Yes".equals(editFlagCriteria);
	}//canEditFlagCriteria
	public boolean canEditForcedFlags(){
		return "Yes".equals(editForcedFlags);
	}//canEditForcedFlags
	public boolean canEditNotes(){
		return "Yes".equals(editNotes);
	}//canEditNotes
	
	public void resetFields(){
		id = "";
		name = "";
		username = "";
		password = "";
		email = "";
		dateCreated = "";
		lastLogin = "";
		seeOsha = "";
		seeFullPQF = "";
		editFlagCriteria = "";
		editForcedFlags = "";
		editNotes = "";
		accountID = "";
	}//resetFields

	public void setFromDB(String uID) throws Exception {
		id = uID;
		setFromDB();
	}//setFromDB

	public void setFromDB() throws Exception {
		String selectQuery = "SELECT * FROM users LEFT OUTER JOIN permissions ON users.id=permissions.user_id WHERE users.id="+
			id+" ORDER BY name ASC;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			else
				throw new Exception("No account with id: "+id);
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setFromDB

	public void setFromRequest(javax.servlet.http.HttpServletRequest request, String id) throws Exception {
		name =request.getParameter("name_"+id);
		username = request.getParameter("username_"+id);
		password = request.getParameter("password_"+id);
		email = request.getParameter("email_"+id);
		seeOsha = Utilities.getIsChecked(request.getParameter("seeOsha_"+id));
		seeFullPQF = Utilities.getIsChecked(request.getParameter("seeFullPQF_"+id));
		editFlagCriteria = Utilities.getIsChecked(request.getParameter("editFlagCriteria_"+id));
		editForcedFlags = Utilities.getIsChecked(request.getParameter("editForcedFlags_"+id));
		editNotes = Utilities.getIsChecked(request.getParameter("editNotes_"+id));
	}//setFromRequest

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		id = SQLResult.getString("id");
		name = SQLResult.getString("name");
		username = SQLResult.getString("username");
		password = SQLResult.getString("password");
		email = SQLResult.getString("email");
		dateCreated = DateBean.toShowFormat(SQLResult.getString("dateCreated"));
		lastLogin = DateBean.toShowFormat(SQLResult.getString("lastLogin"));
		seeOsha = SQLResult.getString("seeOsha");
		seeFullPQF = SQLResult.getString("seeFullPQF");
		editFlagCriteria = SQLResult.getString("editFlagCriteria");
		editForcedFlags = SQLResult.getString("editForcedFlags");
		editNotes = SQLResult.getString("editNotes");
		accountID = SQLResult.getString("accountID");
	}//setFromResultSet

	//Gets username and passwords associated with account (i.e. operator)
	//Brittney 11-9-04
/*	public String[] getAccountUsers(String accountID) throws Exception {
		ArrayList usersAL = new ArrayList();
		DBReady();
		String Query = "SELECT * FROM users LEFT OUTER JOIN permissions ON users.id=permissions.user_id WHERE users.accountID="+
			accountID+" ORDER BY name ASC;";
		ResultSet SQLResult = SQLStatement.executeQuery(Query);
		while (SQLResult.next()) {
			usersAL.add(SQLResult.getString("id"));
			usersAL.add(SQLResult.getString("name"));
			usersAL.add(SQLResult.getString("username"));
			usersAL.add(SQLResult.getString("password"));
			usersAL.add(SQLResult.getString("seeOsha"));
			usersAL.add(SQLResult.getString("seeFullPQF"));
		}//while
		DBClose();
	return (String[])usersAL.toArray(new String[0]);
	}//getAccountUsers
*/
	//Updates row in users table Brittney 11-9-04
	public void writeToDB(String id) throws Exception {
		String updateQuery = "UPDATE users SET name='"+eqDB(name)+"',username='"+eqDB(username)+
			"',password='"+eqDB(password)+"',email='"+eqDB(email)+"' "+
			"WHERE id="+id+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
			updateQuery = "UPDATE permissions SET "+
				"seeOsha='"+seeOsha+
				"',seeFullPQF='"+seeFullPQF+
				"',"+"editFlagCriteria='"+editFlagCriteria+
				"',editForcedFlags='"+editForcedFlags+
				"',editNotes='"+editNotes+
				"' WHERE user_id="+id+";";
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//updateUser

	//Adds row to users  & permissions table Brittney 11-9-04
	public void addUser(String accountID) throws Exception {
		String insertQuery = "INSERT INTO users (name,username,password,email,dateCreated,accountID) VALUES ('"+
			eqDB(name)+"','"+
			eqDB(username)+"','"+
			eqDB(password)+"','"+
			eqDB(email)+"',"+
			"NOW(),"+
			accountID+");";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ResultSet SQLResult = SQLStatement.getGeneratedKeys();
			if (SQLResult.next()) {
				String user_id = SQLResult.getString("GENERATED_KEY");
				insertQuery = "INSERT INTO permissions (user_id,seeOsha,seeFullPQF,editFlagCriteria,"+
					"editForcedFlags,editNotes) VALUES ("+
					user_id+",'"+seeOsha+"','"+seeFullPQF+"','"+editFlagCriteria+"','"+
					editForcedFlags+"','"+editNotes+"')";
				SQLStatement.executeUpdate(insertQuery);
			} //if
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//addUser
	
	//Deletes row from users table Brittney 11-9-04
	public void deleteUser(String dID) throws Exception {
		String deleteQuery = "DELETE FROM users WHERE id="+dID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM permissions WHERE user_id="+dID+";";
			SQLStatement.executeUpdate(deleteQuery);
		}finally{
			DBClose();
		}//finally
	}//deleteUser
	
	public void setList(String accountID) throws Exception {
		String selectQuery = "SELECT * FROM users LEFT OUTER JOIN permissions ON users.id=permissions.user_id WHERE users.accountID="+
			accountID+" ORDER BY name ASC;";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch(Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setList

	public void resetList() throws Exception {
		try{
			listRS.beforeFirst();
			count = 0;
		}catch(Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//resetList

	public boolean isNextRecord() throws Exception {
		try{
			if (!(count <= numResults && listRS.next()))
				return false;
			setFromResultSet(listRS);
			count++;
			return true;
		}catch(Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//isNextRecord

	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		if (null != listRS) {
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}//closeList

	public boolean isOK() throws Exception {
		errorMessages = new Vector<String>();
		if (name.length() < 5)
			errorMessages.addElement("Please choose a name at least 5 characters long");
		if (username.length() < 5)
			errorMessages.addElement("Please choose a username at least 5 characters long");
		if (password.length() < 5)
			errorMessages.addElement("Please choose a password at least 5 characters long.");
		if ((0==email.length()) || (!Utilities.isValidEmail(email)))
			errorMessages.addElement("Please enter a valid email address.");
		if (password.equalsIgnoreCase(username))
			errorMessages.addElement("Please choose a password different from your username.");
		return (errorMessages.size() == 0);
	}//isOK

	public String eqDB(String temp) {
		return Utilities.escapeQuotes(temp);
	}//eqDB

	public String toString(){
		return "name="+name+","+
			"username="+username+","+
			"password="+password+","+
			"email="+email+","+
			"seeOsha="+seeOsha+","+
			"seeFullPQF="+seeFullPQF+","+
			"editFlagCriteria="+editFlagCriteria+","+
			"editForcedFlags="+editForcedFlags+","+
			"editNotes="+editNotes;
	}//toString
}//UserBean