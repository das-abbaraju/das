package com.picsauditing.domain;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.picsauditing.PICS.DataBean;

import com.picsauditing.PICS.Utilities;

public class UsersDO extends DataBean implements IPicsDO {
	
	private int id;
	private String username;
	private String password;
	private String isGroup;
	private String email;
	private String name;
	private String isActive;
	private Date dateCreated;
	private Date lastLogin;
	public String passwordChange = "";
	
	public String oldPassword = ""; // used for determining if the password has changed 
	
	private static final int PASSWORD_DURATION = 365; // days between required password update
	public static final int MIN_PASSWORD_LENGTH = 5; // minimum required length of a passord
	//private int accountID;
	

	@Override
	public void setFromRequest(HttpServletRequest request) {
		
		SimpleDateFormat DBFormat = new SimpleDateFormat("yyyy-MM-dd");
		id = Integer.parseInt(request.getParameter("id"));
		username = request.getParameter("username");
		password = request.getParameter("password");
		isGroup = request.getParameter("isGroup");
		email = request.getParameter("email");
		name = request.getParameter("name");
		isActive = request.getParameter("isActive");
		
		try{
			dateCreated = DBFormat.parse(request.getParameter("dateCreated"));
			lastLogin = DBFormat.parse(request.getParameter("lastLogin"));
			//accountID = Integer.parseInt(request.getParameter("accountID"));
		}catch(Exception ex){
			
		}

	}

	@Override
	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		id = SQLResult.getInt("id");
		username = SQLResult.getString("username");
		password = SQLResult.getString("password");
		isGroup = SQLResult.getString("isGroup");
		email = SQLResult.getString("email");
		name = SQLResult.getString("name");
		isActive = SQLResult.getString("isActive");
		dateCreated = SQLResult.getDate("dateCreated");
		lastLogin = SQLResult.getDate("lastLogin");
		//accountID = SQLResult.getInt("accountID");

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	public String getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	/*
	public int getAccountID() {
		return accountID;
	}

	public void setAccountID(int accountID) {
		this.accountID = accountID;
	}
	
	*/
	
	public void setFromDB(int aID) throws Exception {
		id = aID;
		setFromDB();
	}//setFromDB
	
	public void setFromDB() throws Exception {
		String selectQuery = "SELECT * FROM users WHERE id=" + id +";";
		try {
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

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE users SET name='"+eqDB(name)+
			"',username='"+eqDB(username)+"',password='"+eqDB(password)+
			"',email='"+eqDB(email)+"' WHERE id="+id+";";
		try {
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
			if (!"".equals(oldPassword) && !oldPassword.equals(password))
				changePassword(password);
		}finally{
			DBClose();
		}//finally
	}//writeToDB
	
	public boolean isOK() throws Exception {
		errorMessages = new Vector<String>();
		if (name.length() == 0)
			errorMessages.addElement("Please fill in the Company Name field");
		if (name.length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long");
		if (username.length() == 0)
			errorMessages.addElement("Please fill in the Username field");
		if (password.length() < MIN_PASSWORD_LENGTH)
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
		if (password.equalsIgnoreCase(username))
			errorMessages.addElement("Please choose a password different from your username.");
		//Don't chekc these fields if auditor BJ 10-28-04
				
		if ((email.length() == 0) || (!Utilities.isValidEmail(email)))
			errorMessages.addElement("Please enter a valid email address. This is our main way of communicating with you so it must be valid");
		return (errorMessages.size() == 0);
	}
	
	public void changePassword(String newPassword) throws Exception {
		password = newPassword;
		String updateQuery = "UPDATE users SET password='"+password+"', passwordChange=NOW() "+
			"WHERE id="+id+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//changePassword
	
	public boolean mustChangePassword() throws Exception {
		Calendar todayCal = Calendar.getInstance();
		SimpleDateFormat toDBFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date passwordChangeDate = toDBFormat.parse(passwordChange);
		Calendar passwordChangeCal = Calendar.getInstance();
		passwordChangeCal.setTime(passwordChangeDate);
		int passwordChangeDays = passwordChangeCal.get(Calendar.DAY_OF_YEAR);
		int dayDays = todayCal.get(Calendar.DAY_OF_YEAR);
		int yearDifference =  (todayCal.get(Calendar.YEAR) - passwordChangeCal.get(Calendar.YEAR));
		int daysDifference = 365 * yearDifference;
		int daysPassed = dayDays - passwordChangeDays + daysDifference;

		if (daysPassed > PASSWORD_DURATION)
			return true;
		return false;
	}//mustChangePassword

	public boolean newPasswordOK(String newPassword) {
		if (newPassword.equalsIgnoreCase(password)) {
			errorMessages.addElement("You entered the same password.  Please choose a new one.");
			return false;
		}//if
		if (newPassword.equalsIgnoreCase(username)) {
			errorMessages.addElement("Please choose a password different from your username.");
			return false;
		}//if
		if (newPassword.length() < MIN_PASSWORD_LENGTH) {
			errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH + " characters in length.");
			return false;
		}//if
		return true;
	}//newPasswordOK
	
	

}
