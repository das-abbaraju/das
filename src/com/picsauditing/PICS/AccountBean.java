package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

public class AccountBean extends DataBean {
	protected static final String ADMIN_ID = "-1";
	public static final String ALL_ACCESS = "-1";
	public static final String CREATED_BY_PICS = "PICS"; // Must match createdBy ENUM in DB table
	public static final String CREATED_BY_INTERNET = "Internet"; // Must match createdBy ENUM in DB table
	private static final int PASSWORD_DURATION = 365; // days between required password update
	public static final int MIN_PASSWORD_LENGTH = 5; // minimum required length of a passord
	private static final boolean INCLUDE_PICS = true;
	private static final boolean DONT_INCLUDE_PICS = false;
	static final String[] INDUSTRY_ARRAY = {"Petrochemical","Mining","Power","General",
											"Construction","Manufacturing"};
	OperatorBean o = null;
	String accountDate = "";
	String canEditPrequal = "";
	public boolean isMainAccount = false; //used in check login to see if user is not in main accounts table, but in users table
	public String userID = "0"; //used in check login, to set id for users not in main accounts table, but in users table
	public HashSet<String> canSeeSet = new HashSet<String>(); // all sub contractors of a general contractor/operator
	public HashSet<String> hasCertSet = new HashSet<String>(); // all contractors with insurance certs for a general contractor/operator
	public HashSet<String> auditorCanSeeSet = new HashSet<String>(); // all contractors assigned  auditor
	public HashSet<String> auditorOfficeSeeSet = new HashSet<String>(); // all contractors assigned  office auditor
	public HashSet<String> auditorDesktopSeeSet = new HashSet<String>(); // all contractors assigned  desktop auditor
	
	public String id = "";
	public String type = "Contractor"; // is of type ENUM of ('Contractor','Operator','General')
	public String name = "";
	public String username = "";
	public String password = "";
	public String passwordChange = "";
	public String lastLogin = "1/1/01";
	public String contact = "";
	public String address = "";
	public String city = "";
	public String state = "";
	public String zip = "";
	public String phone = "";
	public String phone2 = "";
	public String fax = "";
	public String email = "";
	public String web_URL = "";
	public String industry = "";
	public String active = "N";
	public String createdBy = "";
	public String dateCreated = "";
	public String oldPassword = ""; // used for determining if the password has changed 
	public String emailConfirmedDate = "";
	boolean updatedPassword = false;
	public String prevLastLogin = "1/1/01";
	public void setId(String s) {id = s;}//setId
	public void setActive(String s) {active = s;}//setActive
	public String getId() {return id;}//getId
	public String getFullAddress() {
		return address + '\n' + city + ", " + state + " " + zip;		
	}//getFullAddress
	public void setOBean() {
		if (null == o)
			o = new OperatorBean();
	}//setOBean

//	jj 6-12-06
//	public String getAccessID() {
//		if ("Y".equals(seesAll))	return ALL_ACCESS;
//		else	return id;
//	}//getAccessID
	public boolean isActive() {
		if ((active != null) && ("N".equals(active)))
			return false;
		return true;
	}//isActive
	public String getActiveChecked() {
		if ((active != null) && ("Y".equals(active)))
			return "checked";
		return "";
	}//getActiveChecked
	public String getNotActiveChecked() {
		if ((active != null) && ("N".equals(active)))
			return "checked";
		return "";
	}//getNotActiveChecked
	public String typeGeneralChecked() {
		if ("General".equals(type))	return "checked";
		else return "";
	}//typeGeneralChecked
	public String typeContractorChecked() {
		if ("Contractor".equals(type))	return "checked";
		else	return "";
	}//typeContractorChecked
	public String getPhone2Header() {
		if ("".equals(phone2))	return "";
		else	return " | Phone 2:";
	}//getPhone2Header
	public String getFaxLine() {
		if ("".equals(fax))	return "";
		else	return "<span class=redMain>Fax:</span> "+fax+"<br>";
	}//getFaxLine
	public String getWeb_URLHeader() {
		if ("".equals(web_URL))	return "";
		else	return "Web:";
	}//getWeb_URLHeader
	public void setFromDB(String aID) throws Exception {
		id = aID;
		setFromDB();
	}//setFromDB
	
	public void setAuditorFromDB(String aID) throws Exception {
		id = aID;
		setFromDB();
	}//setFromDB

	public void setFromDB() throws Exception {
		String selectQuery = "SELECT * FROM accounts WHERE id="+Utilities.intToDB(id)+";";
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
	
	public void setAuditorFromDB() throws Exception {
		String selectQuery = "SELECT * FROM accounts WHERE id="+Utilities.intToDB(id)+";";
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


	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		try {
			id = SQLResult.getString("id");
			type = SQLResult.getString("type");
			name = SQLResult.getString("name");
			username = SQLResult.getString("username");
			password = SQLResult.getString("password");
			passwordChange = SQLResult.getString("passwordChange");
			lastLogin = DateBean.toShowFormat(SQLResult.getString("lastLogin"));
			contact = SQLResult.getString("contact");
			address = SQLResult.getString("address");
			city = SQLResult.getString("city");
			state = SQLResult.getString("state");
			zip = SQLResult.getString("zip");
			phone = SQLResult.getString("phone");
			phone2 = SQLResult.getString("phone2");
			fax = SQLResult.getString("fax");
			email = SQLResult.getString("email");
			web_URL = SQLResult.getString("web_URL");
			industry = SQLResult.getString("industry");
			active = SQLResult.getString("active");
			createdBy = SQLResult.getString("createdBy");
			dateCreated = DateBean.toShowFormat(SQLResult.getString("dateCreated"));
			emailConfirmedDate = DateBean.toShowFormat(SQLResult.getString("emailConfirmedDate"));
			oldPassword = password;
		} catch (Exception ex) {
			DBClose();
			throw ex;
		}//catch
	}//setFromResultSet

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE accounts SET type='"+type+"',name='"+eqDB(name)+
			"',username='"+eqDB(username)+"',password='"+eqDB(password)+"',lastLogin='"+DateBean.toDBFormat(lastLogin)+
			"',contact='"+eqDB(contact)+
			"',address='"+eqDB(address)+"',city='"+eqDB(city)+"',state='"+eqDB(state)+"',zip='"+eqDB(zip)+
			"',phone='"+eqDB(phone)+"',phone2='"+eqDB(phone2)+"',fax='"+eqDB(fax)+"',email='"+eqDB(email)+
			"',web_URL='"+eqDB(web_URL)+"',industry='"+eqDB(industry)+
			"',active='"+active+"',createdBy='"+createdBy+
			"' WHERE id="+id+";";
		try {
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
			if (!"".equals(oldPassword) && !oldPassword.equals(password))
				changePassword(password);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void writeAuditorToDB() throws Exception {
		String updateQuery = "UPDATE accounts SET name='"+eqDB(name)+
			"',username='"+eqDB(username)+"',password='"+eqDB(password)+
			"',email='"+eqDB(email)+"',active='"+active+
			"' WHERE id='"+id+"';";
		try {
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
			if (!"".equals(oldPassword) && !oldPassword.equals(password))
				changePassword(password);
		}finally{
			DBClose();
		}//finally
	}//writeAuditorToDB

	public boolean writeNewToDB() throws Exception {
		if (usernameExists(username)) {
			errorMessages.addElement("The username <b>"+username+"</b> already exists.  Please choose another username.");
			return false;
		}//if
		String insertQuery = "INSERT INTO accounts (id,type,name,username,password,passwordChange,lastLogin,"+
			"contact,address,city,state,zip,phone,phone2,fax,email,web_URL,industry,active,createdBy,"+
			"dateCreated) VALUES (''+0,'"+type+"','"+eqDB(name)+
			"','"+eqDB(username)+"','"+eqDB(password)+"',NOW(),'"+DateBean.toDBFormat(lastLogin)+"','"+eqDB(contact)+"','"+eqDB(address)+"','"+city+
			"','"+eqDB(state)+"','"+eqDB(zip)+"','"+eqDB(phone)+"','"+eqDB(phone2)+"','"+eqDB(fax)+"','"+eqDB(email)+"','"+eqDB(web_URL)+
			"','"+eqDB(industry)+"','"+active+"','"+createdBy+"',NOW());";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			ResultSet SQLResult = SQLStatement.getGeneratedKeys();
			if (SQLResult.next())
				id = SQLResult.getString("GENERATED_KEY");
			else {
				SQLResult.close();
				DBClose();
				throw new Exception("No id returned after inserting new account");
			}//else
			SQLResult.close();
			return true;
		}finally{
			DBClose();
		}//finally
	}//writeNewToDB

	public void setFromRequest(javax.servlet.http.HttpServletRequest r) throws Exception {
		name = r.getParameter("name");
		username = r.getParameter("username");
		password = r.getParameter("password");
		contact = r.getParameter("contact");
		address = r.getParameter("address");
		city = r.getParameter("city");
		state = r.getParameter("state");
		zip = r.getParameter("zip");
		phone = r.getParameter("phone");
		phone2 = r.getParameter("phone2");
		fax = r.getParameter("fax");
		email = r.getParameter("email");
		web_URL = r.getParameter("web_URL");
		industry = r.getParameter("industry");
		active = r.getParameter("active");
		createdBy = r.getParameter("createdBy");
		type = r.getParameter("type");
	}//setFromRequest

	public void setFromUploadRequest(HttpServletRequest r) throws Exception {
		Map<String,String> m = (Map<String,String>)r.getAttribute("uploadfields");
		name = m.get("name");
		username = m.get("username");
		password = m.get("password");
		contact = m.get("contact");
		address = m.get("address");
		city = m.get("city");
		state = m.get("state");
		zip = m.get("zip");
		phone = m.get("phone");
		phone2 = m.get("phone2");
		fax = m.get("fax");
		email = m.get("email");
		web_URL = m.get("web_URL");
		industry = m.get("industry");
		active = m.get("active");
		createdBy = m.get("createdBy");
		type = m.get("type");
	}//setFromUploadRequest

	public void setFromUploadRequestClientNew(javax.servlet.http.HttpServletRequest r) throws Exception {
		name = r.getParameter("name");
		username = r.getParameter("username");
		password = r.getParameter("password");
		contact = r.getParameter("contact");
		address = r.getParameter("address");
		city = r.getParameter("city");
		state = r.getParameter("state");
		zip = r.getParameter("zip");
		phone = r.getParameter("phone");
		phone2 = r.getParameter("phone2");
		fax = r.getParameter("fax");
		email = r.getParameter("email");
		web_URL = r.getParameter("web_URL");
		industry = r.getParameter("industry");
		active = "N";
		createdBy = CREATED_BY_INTERNET;
		type = "Contractor";
	}//setFromUploadRequestClientNew

	public void setFromUploadRequestClientEdit(HttpServletRequest r) throws Exception {
		Map<String,String> m = (Map<String,String>)r.getAttribute("uploadfields");
		name = m.get("name");
		password = m.get("password");
		contact = m.get("contact");
		address = m.get("address");
		city = m.get("city");
		state = m.get("state");
		zip = m.get("zip");
		phone = m.get("phone");
		phone2 = m.get("phone2");
		fax = m.get("fax");
		email = m.get("email");
		web_URL = m.get("web_URL");
		industry = m.get("industry");
	}//setFromUploadRequestClientEdit

	public void setFromRequestNewAuditor(javax.servlet.http.HttpServletRequest r) throws Exception {
		name = r.getParameter("name");
		username = r.getParameter("username");
		password = r.getParameter("password");
		email = r.getParameter("email");
		active = r.getParameter("active");
		type = "Auditor";
		createdBy = CREATED_BY_PICS;
	}//setFromRequestNewAuditor

	public String searchID(String search_name) throws Exception {
		String selectQuery = "SELECT id from accounts WHERE name='"+eqDB(search_name)+"';";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			SQLResult.next();
			id = SQLResult.getString("id");
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		return id;	
	}//searchID

	public boolean usernameExists(String u_name) throws Exception {
		boolean nameExists = false;
		try {
			DBReady();
			String selectQuery = "SELECT id FROM accounts WHERE username='"+eqDB(u_name)+"' "+
				"UNION "+
				"SELECT id FROM users WHERE username='"+eqDB(u_name)+"' ";
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				nameExists = true;
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		return nameExists;
	}//usernameExists

	public boolean checkLogin(String lname, String lpass, javax.servlet.http.HttpServletRequest req) throws Exception {
		try {
			boolean isUser;
			String selectQuery = "SELECT accounts.*, users.id AS uID, users.isActive, users.password AS user_password "+
			"FROM accounts JOIN users ON accounts.id = users.accountID "+
			"WHERE users.username='"+eqDB(lname)+"' UNION " +
			"SELECT *, null AS uID, null AS isActive, null AS user_password "+
			"FROM accounts "+
			"WHERE username='"+eqDB(lname)+"' AND type <> 'Operator' ";

			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			
			if (!SQLResult.next()) {
				errorMessages.addElement("No account exists with that username");
				SQLResult.close();
				logLoginAttempt(req,lname,lpass,"N");
				return false;
			}
			if (!SQLResult.isLast()) {
				errorMessages.addElement("We found more than one account with that username.<br>Please contact PICS to fix this problem.");
				SQLResult.close();
				logLoginAttempt(req,lname,"*","N");
				DBClose();
				return false;
			}
			// Is this result a user or an account?
			isUser = SQLResult.getInt("uID") > 0;
			boolean canLogin;
			String password;
			if (isUser) {
				canLogin = SQLResult.getString("isActive").startsWith("Y");
				password = SQLResult.getString("user_password");
				userID = SQLResult.getString("uID");
			} else {
				canLogin = SQLResult.getString("active").startsWith("Y");
				password = SQLResult.getString("password");
				this.userID = "";
			}
			
			if (!canLogin) {
				errorMessages.addElement("That account is currently inactive.<br>Please contact PICS to activate your account.");
				SQLResult.close();
				logLoginAttempt(req,lname,"*","N");
				DBClose();
				return false;
			}
			
			if (!password.equals(lpass)) {
				errorMessages.addElement("Invalid password attempt");
				SQLResult.close();
				logLoginAttempt(req,lname,lpass,"N");
				DBClose();
				return false;
			}//if not active
			
			///////////////////////////////
			// Login attempt successful  //
			///////////////////////////////
			
			
			// set the account object and discard results
			setFromResultSet(SQLResult);
			SQLResult.close();
			prevLastLogin = lastLogin;
			
			// Update the lastLogin attempt on both accounts and users tables
			String updateQuery = "UPDATE accounts SET lastLogin=NOW() WHERE id="+id;
			SQLStatement.executeUpdate(updateQuery);
			//isMainAccount = (lname.equalsIgnoreCase(username) && lpass.equalsIgnoreCase(password));
			//if (!isMainAccount){
			if (isUser) {
				updateQuery = "UPDATE users SET lastLogin=NOW() WHERE id="+userID;
				SQLStatement.executeUpdate(updateQuery);
			}
			
			// set these for isFirstLogin(), and mustSubmitPQF()
			selectQuery = "SELECT accountDate, canEditPrequal FROM contractor_info WHERE id="+id+";";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()){
				accountDate = DateBean.toShowFormat(SQLResult.getString("accountDate"));
				canEditPrequal = SQLResult.getString("canEditPrequal");
			}//if
			SQLResult.close();
			
			// Set canSeeSet
			canSeeSet = new HashSet<String>();
			if ("Contractor".equals(type))
				canSeeSet.add(id);
			selectQuery = "SELECT subID FROM accounts INNER JOIN generalcontractors ON (id=subID) "+
			"WHERE active='Y' AND genID="+id+";";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				canSeeSet.add(SQLResult.getString("subID"));
			SQLResult.close();
			
			/*
			 * Moved to PermissionsBean
			 * Trevor 1/16/2008
			auditorCanSeeSet = new HashSet<String>();
			auditorCanSeeSet.add(id);
			selectQuery = "SELECT id FROM contractor_info WHERE auditor_id="+id+" OR desktopAuditor_id="+id+" OR pqfAuditor_id="+id+";";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				auditorCanSeeSet.add(SQLResult.getString("id"));
			SQLResult.close();
			 */
			
			// Set hasCertSet
			hasCertSet = new HashSet<String>();
			selectQuery = "SELECT contractor_id FROM certificates WHERE operator_id="+id+";";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				hasCertSet.add(SQLResult.getString("contractor_id"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		return true;
	}//checkLogin

	public void logLoginAttempt(javax.servlet.http.HttpServletRequest r, String lname, 
								String lpass, String success) throws Exception {
		String remoteAddress = r.getRemoteAddr();
		String insertQuery = "INSERT INTO loginLog (company,type,username,password,successful,date,remoteAddress," +
			"id) VALUES ('"+eqDB(name)+"','"+type+"','"+eqDB(lname)+"','"+eqDB(lpass)+"','"+success+"',NOW(),'"+remoteAddress+
			"',"+Utilities.intToDB(id)+");";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//logLoginAttempt

	public String[] getActiveOperatorsArray(boolean includePICS) throws Exception {
		setOBean();
		return o.getOperatorsArray(includePICS, OperatorBean.DONT_INCLUDE_ID, OperatorBean.DONT_INCLUDE_GENERALS, OperatorBean.ONLY_ACTIVE);
	}//getActiveOperatorsArray

	public String[] getActiveGeneralsArray(boolean includePICS) throws Exception {
		setOBean();
		return o.getOperatorsArray(includePICS, OperatorBean.DONT_INCLUDE_ID, OperatorBean.INCLUDE_GENERALS, OperatorBean.ONLY_ACTIVE);
	}//getActiveGeneralsArray

	public String[] getAuditorContractors(String auditor_id, String orderBy) throws Exception {
		ArrayList<String> audContractors = new ArrayList<String>();
		String selectQuery = "SELECT * FROM accounts inner join contractor_info on accounts.id = contractor_info.id " +
			 " WHERE contractor_info.auditor_id="+auditor_id+" ORDER BY "+orderBy+";";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				audContractors.add(SQLResult.getString("id"));
				audContractors.add(SQLResult.getString("name"));
				audContractors.add(DateBean.toShowFormat(SQLResult.getString("assignedDate")));
				audContractors.add(SQLResult.getString("auditStatus"));
				audContractors.add(DateBean.toShowFormat(SQLResult.getString("auditDate")));
			}//while
		}finally{
			DBClose();
		}//finally
		return (String[])audContractors.toArray(new String[0]);
	}//getAuditorContractors

	public String getOperatorSelect(String name, String classType, String selectedOperator) throws Exception {
		return Utilities.inputSelect(name, classType, selectedOperator, getActiveOperatorsArray(DONT_INCLUDE_PICS));
	}//getOperatorSelect

	public String getGeneralSelect(String name, String classType, String selectedOperator) throws Exception {
		return Utilities.inputSelect(name, classType, selectedOperator, getActiveGeneralsArray(DONT_INCLUDE_PICS));
	}//getGeneralSelect

	public void deleteAccount(String deleteID, String path) throws Exception {
	// called aBean.deleteAccount(action_id, config.getServletContext().getRealPath("/"));
		try {
			DBReady();
			String deleteQuery = "DELETE FROM accounts WHERE id="+deleteID+" LIMIT 1;";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM contractor_info WHERE id="+deleteID+" LIMIT 1;";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM generalContractors WHERE subID="+deleteID+" OR genID="+deleteID+";";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM billingInfo WHERE id="+deleteID+";";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM auditData WHERE con_id="+deleteID+";";
			SQLStatement.executeUpdate(deleteQuery);
//			Query = "DELETE FROM OSHA WHERE conID="+deleteID+";";
//			SQLStatement.executeUpdate(Query);
		}finally{
			DBClose();
		}//finally
		OperatorBean.resetSubCountTable();
		//Delete certificates in DB and files
		new CertificateBean().deleteAllCertificates(deleteID,path);
		// Delete files (audit, manual, prequal, update, logo, certificates)
		java.io.File f = null;
		f = new java.io.File(path+"files/manuals/manual_"+deleteID+".pdf");
		if (f.exists())		f.delete();
		f = new java.io.File(path+"files/brochures/brochure_"+deleteID+".pdf");
		if (f.exists())		f.delete();
		f = new java.io.File(path+"files/updates/update_"+deleteID+".pdf");
		if (f.exists())		f.delete();
		f = new java.io.File(path+"logos/logo_"+deleteID+".gif");
		if (f.exists())		f.delete();
		f = new java.io.File(path+"logos/logo_"+deleteID+".jpg");
		if (f.exists())		f.delete();
		f = new java.io.File(path+"logos/logo_"+deleteID+".bmp");
		if (f.exists())		f.delete();
		OSHABean oBean = new OSHABean();
		oBean.setListFromDB(deleteID);
		while (oBean.hasNext()) {
			f = new java.io.File(path+"files/oshas/osha1_"+oBean.OID+".pdf");
			if (f.exists())		f.delete();
			f = new java.io.File(path+"files/oshas/osha2_"+oBean.OID+".pdf");
			if (f.exists())		f.delete();
			f = new java.io.File(path+"files/oshas/osha3_"+oBean.OID+".pdf");
			if (f.exists())		f.delete();
		}//while
	}//deleteAccount

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

	public void changePassword(String newPassword) throws Exception {
		password = newPassword;
		String updateQuery = "UPDATE accounts SET password='"+password+"', passwordChange=NOW() "+
			"WHERE id="+id+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally
	}//changePassword

	public boolean sendPasswordEmail(String email) throws Exception {
		if (!Utilities.isValidEmail(email)) {
			errorMessages.addElement("Please enter a valid email address.");
			return false;
		}//if
		String selectQuery = "SELECT * FROM accounts WHERE email='"+email+"' LIMIT 2";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (!SQLResult.next()) {
				selectQuery = "SELECT * FROM users LEFT OUTER JOIN permissions ON users.id=permissions.user_id WHERE email='"+email+"';";
				SQLResult = SQLStatement.executeQuery(selectQuery);
				if (!SQLResult.next()) {
					errorMessages.addElement("No account in our records has that email address.  Please verify it is " +
					"the one you used when creating your PICS company profile.");
					SQLResult.close();
					DBClose();
					return false;		
				}//if
				UserBean uBean = new UserBean();
				uBean.setFromResultSet(SQLResult);
				com.picsauditing.PICS.EmailBean.sendPasswordEmail(uBean.accountID,uBean.username,uBean.password,uBean.email,uBean.name);
			}else{
				setFromResultSet(SQLResult);
				com.picsauditing.PICS.EmailBean.sendPasswordEmail(id,username,password,email,contact);
			}//else
			errorMessages.addElement("An email has been sent to this address: <b>" + email + "</b> with your " +
			"PICS account login information");
			SQLResult.close();
			DBClose();
			return true;
		}finally{
			DBClose();
		}//finally
	}//sendPasswordEmail

	public static String getIndustrySelect(String name, String classType, String selectedIndustry) throws Exception {
		return Utilities.inputSelect(name, classType, selectedIndustry, INDUSTRY_ARRAY);
	}//getIndustrySelect

	public String getGeneralSelectMultiple(String name, String classType, String[] selectedContractors) throws Exception {
		setOBean();
		String[] generals = o.getOperatorsArray(OperatorBean.DONT_INCLUDE_PICS, OperatorBean.INCLUDE_ID, OperatorBean.INCLUDE_GENERALS, OperatorBean.INCLUDE_INACTIVE);
		return Utilities.inputMultipleSelect2Multiples(name, classType, "3", selectedContractors, generals);
	}//getGeneralSelectMultiple

//****** not sure to include only actives
	public String getGeneralSelect2(String name, String classType, String selectedOption, boolean listDefault) throws Exception {
		setOBean();
		String[] generals = o.getOperatorsArray(OperatorBean.DONT_INCLUDE_PICS, OperatorBean.INCLUDE_ID, OperatorBean.INCLUDE_GENERALS, OperatorBean.ONLY_ACTIVE);
		if (listDefault)
			return Utilities.inputSelect2First(name, classType, selectedOption, generals, 
											SearchBean.DEFAULT_GENERAL_VALUE, 
											SearchBean.DEFAULT_GENERAL);
		else
			return Utilities.inputSelect2(name, classType, selectedOption, generals);
	}//getGeneralContractorsSelect2
	
	public String getGeneralSelect3(String name, String classType, String selectedOption, boolean listDefault, String contractor_id) throws Exception {
		setOBean();
		o.canSeeInsurance = "Yes";
		String ret = "";
		String[] generals = o.getOperatorsArray(OperatorBean.DONT_INCLUDE_PICS, OperatorBean.INCLUDE_ID, OperatorBean.INCLUDE_GENERALS, OperatorBean.ONLY_ACTIVE);
		
		String[] filteredGenerals = filteredGenerals(generals, contractor_id);
		
		if (listDefault)
			ret = Utilities.inputSelect2First(name, classType, selectedOption, filteredGenerals, 
											SearchBean.DEFAULT_GENERAL_VALUE, 
											SearchBean.DEFAULT_GENERAL);
		else
			ret = Utilities.inputSelect2(name, classType, selectedOption, filteredGenerals);
		o.canSeeInsurance = "No";
		return ret;		
	}//getGeneralContractorsSelect3

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
			if (!type.equals("Auditor")) {
				if (contact.length() == 0)
					errorMessages.addElement("Please fill in the Contact field");
				if (address.length() == 0)
					errorMessages.addElement("Please fill in the Address field");
				if (city.length() == 0)
					errorMessages.addElement("Please fill in the City field");
				if (zip.length() == 0)
					errorMessages.addElement("Please fill in the Zip field");
				if (phone.length() == 0)
					errorMessages.addElement("Please fill in the Phone field");
				if (null == type)
					errorMessages.addElement("Please indicate whether this is a general contractor or not");
			} //end if not auditor
			if ((email.length() == 0) || (!Utilities.isValidEmail(email)))
				errorMessages.addElement("Please enter a valid email address. This is our main way of communicating with you so it must be valid");
			return (errorMessages.size() == 0);
		}//isOK
	public boolean isFirstLogin() {
		if ("Contractor".equals(type) && "".equals(accountDate))
			return true;
		return false;
	}//isFirstLogin
	
	public boolean isFirstLoginOfYear(String strLoginStart) throws Exception {
		if ("Contractor".equals(type) && !"".equals(accountDate)){
			String loginStart = strLoginStart + "/" + String.valueOf(DateBean.getCurrentYear());			
			if(DateBean.isFirstBeforeSecond(prevLastLogin,loginStart))
				return true;
		}
		return false;
	}//isFirstLogin
	
	public boolean mustSubmitPQF() {
		if ("Contractor".equals(type) && "Yes".equals(canEditPrequal))
			return true;
		return false;
	}//mustSubmitPQF

	public static boolean isAdminID(String s) {
		return (ADMIN_ID.equals(s));
	}//isAdminID
	
	// 1/15/05 jj - defrags tables
	public void optimizeDB() throws Exception{
		String optimizeQuery = "OPTIMIZE TABLE OSHA,accounts,adminValues,auditCategories,auditData,auditQuestions,certificates,contractor_info,"+
			"form_categories,forms,generalContractors,loginLog,permissions,users;";
		try{
			DBReady();
			SQLStatement.executeUpdate(optimizeQuery);
		}finally{
			DBClose();
		}//finally
	}//optimizeDB
	
	// 4/1/05 bj, called from login, linked from welcome email  contractor. Confirms correct email address 
	// 6/23/05 jj, updating email also activates account
	public void updateEmailConfirmedDate(String username) throws Exception  {
		try{
			DBReady();
			SQLStatement.executeUpdate("UPDATE accounts SET emailConfirmedDate=CURDATE(),"+
					"active='Y' WHERE username='"+username+"'");
		}finally{
			DBClose();
		}//finally		
	}//updateEmailConfirmedDate
	
	public boolean contractorNameExists(String company) throws Exception {
		if (company.length()<3) {
			errorMessages.addElement("Your company name must be at least 3 characters long");		
			return true;
		}//if
		String selectQuery = "SELECT id FROM accounts WHERE UCASE(name) LIKE UCASE('"+eqDB(company)+"%');";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				SQLResult.close();
				DBClose();
				errorMessages.addElement("Someone from your company has already created a PICS account.  Please contact us at: 949.387.1940");
				return true;
			} else {
				SQLResult.close();
				DBClose();
				return false;
			}//else
		}finally{
			DBClose();
		}//finally		
	}//contractorNameExists

	public String eqDB(String temp) {
		return Utilities.escapeQuotes(temp);
	}//eqDB

	private String[] filteredGenerals(String[] generals, String id) throws Exception{
		if(id.equals(""))
			return generals;		
		List<String> retList = new ArrayList<String>();
		String selectQuery = "SELECT genID FROM generalContractors WHERE subid='"+id+"';";
		ResultSet SQLResult;
		try{
			DBReady();
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while(SQLResult.next()){
				String genID = SQLResult.getString("genID");
				for(int i = 0; i < generals.length; i++){
					if(genID.equals(generals[i])){
						retList.add(generals[i]);
						retList.add(generals[i + 1]);
					}//if
				}//for
			}//while
			return (String[])retList.toArray(new String[0]);
		}finally{
			DBClose();
		}//finally
	}//filteredGenerals
	
	public String getName(String id) throws Exception{
		setFromDB(id);
		return name;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public String getPrevLastLogin() {
		return lastLogin;
	}
	public void setPrevLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	
	
	
}//AccountBean
