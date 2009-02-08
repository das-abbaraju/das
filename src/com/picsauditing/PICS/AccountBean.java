package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class AccountBean extends DataBean {
	public static final int MIN_PASSWORD_LENGTH = 5; // minimum required
	// length of a passord
	OperatorBean o = null;
	String accountDate = "";
	public String userID = "0";
	// used in check login, to set id for users not
	// in main accounts table, but in users table
	// subcontractors
	// of a general
	// contractor/operator

	public String id = "";
	public String type = "Contractor"; // is of type ENUM of
	// ('Contractor','Operator','General')
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
	public String creationDate = "";
	public String oldPassword = ""; // used for determining if the password has
	// changed
	public String emailConfirmedDate = "";
	boolean updatedPassword = false;

	public void setId(String s) {
		id = s;
	}

	public void setActive(String s) {
		active = s;
	}

	public String getId() {
		return id;
	}

	public String getFullAddress() {
		return address + '\n' + city + ", " + state + " " + zip;
	}

	public void setOBean() {
		if (null == o)
			o = new OperatorBean();
	}

	public boolean isActive() {
		if ((active != null) && ("N".equals(active)))
			return false;
		return true;
	}

	public String getActiveChecked() {
		if ((active != null) && ("Y".equals(active)))
			return "checked";
		return "";
	}

	public String getNotActiveChecked() {
		if ((active != null) && ("N".equals(active)))
			return "checked";
		return "";
	}

	public String typeGeneralChecked() {
		if ("General".equals(type))
			return "checked";
		else
			return "";
	}

	public String typeContractorChecked() {
		if ("Contractor".equals(type))
			return "checked";
		else
			return "";
	}

	public String getPhone2Header() {
		if ("".equals(phone2))
			return "";
		else
			return " | Phone 2:";
	}

	public String getFaxLine() {
		if ("".equals(fax))
			return "";
		else
			return "<span class=redMain>Fax:</span> " + fax + "<br>";
	}

	public String getWeb_URLHeader() {
		if ("".equals(web_URL))
			return "";
		else
			return "Web:";
	}

	public void setFromDB(String aID) throws Exception {
		id = aID;
		setFromDB();
	}

	public void setAuditorFromDB(String aID) throws Exception {
		id = aID;
		setFromDB();
	}

	public void setFromDB() throws Exception {
		String selectQuery = "SELECT * FROM accounts WHERE id=" + Utilities.intToDB(id) + ";";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			else
				throw new Exception("No account with id: " + id);
			SQLResult.close();
		} finally {
			DBClose();
		}
	}

	public void setAuditorFromDB() throws Exception {
		String selectQuery = "SELECT * FROM accounts WHERE id=" + Utilities.intToDB(id) + ";";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			else
				throw new Exception("No account with id: " + id);
			SQLResult.close();
		} finally {
			DBClose();
		}
	}

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		this.isSet = false;
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
			creationDate = DateBean.toShowFormat(SQLResult.getString("creationDate"));
			emailConfirmedDate = DateBean.toShowFormat(SQLResult.getString("emailConfirmedDate"));
			oldPassword = password;
			this.isSet = true;
		} catch (Exception ex) {
			DBClose();
			throw ex;
		}
	}

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE accounts SET type='" + type + "',name='" + eqDB(name) + "',username='"
				+ eqDB(username) + "',password='" + eqDB(password) + "',lastLogin='" + DateBean.toDBFormat(lastLogin)
				+ "',contact='" + eqDB(contact) + "',address='" + eqDB(address) + "',city='" + eqDB(city) + "',state='"
				+ eqDB(state) + "',zip='" + eqDB(zip) + "',phone='" + eqDB(phone) + "',phone2='" + eqDB(phone2)
				+ "',fax='" + eqDB(fax) + "',email='" + eqDB(email) + "',web_URL='" + eqDB(web_URL) + "',industry='"
				+ eqDB(industry) + "',active='" + active + "',createdBy='" + createdBy + "' WHERE id=" + id + ";";
		try {
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
			if (!"".equals(oldPassword) && !oldPassword.equals(password))
				changePassword(password);
		} finally {
			DBClose();
		}
	}

	public boolean writeNewToDB() throws Exception {
		String insertQuery = "INSERT INTO accounts (id,type,name,username,password,passwordChange,lastLogin,"
				+ "contact,address,city,state,zip,phone,phone2,fax,email,web_URL,industry,active,createdBy,"
				+ "creationDate) VALUES (''+0,'"
				+ type
				+ "','"
				+ eqDB(name)
				+ "','"
				+ eqDB(username)
				+ "','"
				+ eqDB(password)
				+ "',NOW(),'"
				+ DateBean.toDBFormat(lastLogin)
				+ "','"
				+ eqDB(contact)
				+ "','"
				+ eqDB(address)
				+ "','"
				+ city
				+ "','"
				+ eqDB(state)
				+ "','"
				+ eqDB(zip)
				+ "','"
				+ eqDB(phone)
				+ "','"
				+ eqDB(phone2)
				+ "','"
				+ eqDB(fax)
				+ "','"
				+ eqDB(email)
				+ "','"
				+ eqDB(web_URL)
				+ "','"
				+ eqDB(industry)
				+ "','"
				+ active
				+ "','"
				+ createdBy + "',NOW());";
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
			}
			SQLResult.close();
			return true;
		} finally {
			DBClose();
		}
	}

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
	}

	public void setFromUploadRequest(HttpServletRequest r) throws Exception {
		Map<String, String> m = (Map<String, String>) r.getAttribute("uploadfields");
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
	}

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
		createdBy = "Internet";
		type = "Contractor";
	}

	public void setFromUploadRequestClientEdit(HttpServletRequest r) throws Exception {
		Map<String, String> m = (Map<String, String>) r.getAttribute("uploadfields");
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
	}

	public String searchID(String search_name) throws Exception {
		String selectQuery = "SELECT id from accounts WHERE name='" + eqDB(search_name) + "';";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			SQLResult.next();
			id = SQLResult.getString("id");
			SQLResult.close();
		} finally {
			DBClose();
		}
		return id;
	}

	public int findID(String username) throws SQLException {
		int id = 0;
		try {
			DBReady();
			String sql = "SELECT id FROM accounts WHERE username='" + Utilities.escapeQuotes(username) + "';";
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			if (SQLResult.next())
				id = SQLResult.getInt("id");
			SQLResult.close();
		} finally {
			DBClose();
		}
		return id;
	}

	public String[] getActiveGeneralsArray(boolean includePICS) throws Exception {
		setOBean();
		return o.getOperatorsArray(includePICS, OperatorBean.DONT_INCLUDE_ID, OperatorBean.INCLUDE_GENERALS,
				OperatorBean.ONLY_ACTIVE);
	}

	public void deleteAccount(String deleteID, String path) throws Exception {
		// called aBean.deleteAccount(action_id,
		// config.getServletContext().getRealPath("/"));
		try {
			DBReady();
			String deleteQuery = "DELETE FROM pqfcatdata WHERE auditID IN (SELECT auditID FROM contractor_audit WHERE conID = "
					+ deleteID + ");";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM pqfdata WHERE auditID IN (SELECT auditID FROM contractor_audit WHERE conID = "
					+ deleteID + ");";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM osha WHERE conID = " + deleteID + ";";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM contractor_audit WHERE conID = " + deleteID + ";";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM flags WHERE opID=" + deleteID + " OR conID=" + deleteID + ";";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM operators WHERE id=" + deleteID + ";";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM generalContractors WHERE subID=" + deleteID + " OR genID=" + deleteID + ";";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM contractor_info WHERE id=" + deleteID + " LIMIT 1;";
			SQLStatement.executeUpdate(deleteQuery);
			deleteQuery = "DELETE FROM accounts WHERE id=" + deleteID + " LIMIT 1;";
			SQLStatement.executeUpdate(deleteQuery);
		} finally {
			DBClose();
		}// finally
		OperatorBean.resetSubCountTable();
		// Delete certificates in DB and files
		// new CertificateBean().deleteAllCertificates(deleteID, path);
		// Delete files (audit, manual, prequal, update, logo, certificates)
		java.io.File f = null;
		f = new java.io.File(path + "files/manuals/manual_" + deleteID + ".pdf");
		if (f.exists())
			f.delete();
		f = new java.io.File(path + "files/brochures/brochure_" + deleteID + ".pdf");
		if (f.exists())
			f.delete();
		f = new java.io.File(path + "files/updates/update_" + deleteID + ".pdf");
		if (f.exists())
			f.delete();
		f = new java.io.File(path + "logos/logo_" + deleteID + ".gif");
		if (f.exists())
			f.delete();
		f = new java.io.File(path + "logos/logo_" + deleteID + ".jpg");
		if (f.exists())
			f.delete();
		f = new java.io.File(path + "logos/logo_" + deleteID + ".bmp");
		if (f.exists())
			f.delete();
		/*
		 * TODO properly delete new osha records while (oBean.hasNext()) { f =
		 * new java.io.File(path+"files/oshas/osha1_"+oBean.OID+".pdf"); if
		 * (f.exists()) f.delete(); f = new
		 * java.io.File(path+"files/oshas/osha2_"+oBean.OID+".pdf"); if
		 * (f.exists()) f.delete(); f = new
		 * java.io.File(path+"files/oshas/osha3_"+oBean.OID+".pdf"); if
		 * (f.exists()) f.delete(); }//while
		 */
	}

	public void changePassword(String newPassword) throws Exception {
		password = newPassword;
		String updateQuery = "UPDATE accounts SET password='" + password + "', passwordChange=NOW() " + "WHERE id="
				+ id + ";";
		try {
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		} finally {
			DBClose();
		}
	}

	/**
	 * 
	 * @param username
	 * @param id
	 * @return true if the username is a duplicate, false if the username is not
	 *         a duplicate
	 * @throws SQLException
	 */
	public boolean verifyUsername(String username, String id) throws SQLException {
		try {
			DBReady();
			String sQuery = "SELECT * FROM (SELECT id, username FROM accounts UNION SELECT id, username FROM users)"
					+ " t WHERE username = '" + Utilities.escapeQuotes(username) + "'";
			ResultSet SQLResult = SQLStatement.executeQuery(sQuery);
			if (SQLResult.next()) {
				if (id.equals(SQLResult.getString("id")))
					return false;
			} else
				return false;
		} finally {
			DBClose();
		}
		return true;
	}

	public boolean sendPasswordEmail(String email) throws Exception {
		if (!Utilities.isValidEmail(email)) {
			errorMessages.addElement("Please enter a valid email address.");
			return false;
		}
		EmailBuilder emailBuilder = new EmailBuilder();

		try {
			String selectQuery = "SELECT id FROM users WHERE email='" + email + "' LIMIT 2";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				UserDAO dao = (UserDAO) SpringUtils.getBean("UserDAO");
				User user = dao.find(SQLResult.getInt("id"));
				emailBuilder.setTemplate(24); // Password Reminder
				emailBuilder.setUser(user);
				EmailQueue emailQueue = emailBuilder.build();
				emailQueue.setPriority(100);
				EmailSender.send(emailQueue);
			} else {
				selectQuery = "SELECT id FROM accounts WHERE email='" + email + "' and type='Contractor' LIMIT 2";
				SQLResult = SQLStatement.executeQuery(selectQuery);
				if (SQLResult.next()) {
					ContractorAccountDAO conDao = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
					ContractorAccount contractor = conDao.find(SQLResult.getInt("id"));
					emailBuilder.setTemplate(3); // Password Reminder
					emailBuilder.setContractor(contractor);
					EmailQueue emailQueue = emailBuilder.build();
					emailQueue.setPriority(100);
					EmailSender.send(emailQueue);
				} else {
					errorMessages.addElement("No account in our records has that email address.  Please verify it is "
							+ "the one you used when creating your PICS company profile.");
					SQLResult.close();
					DBClose();
					return false;
				}
			}
			errorMessages.addElement("An email has been sent to this address: <b>" + email + "</b> with your "
					+ "PICS account login information");
			return true;
		} finally {
			DBClose();
		}
	}

	public static String getIndustrySelect(String name, String classType, String selectedIndustry) throws Exception {
		String[] industryArray = Strings.convertListToArray(Industry.getValuesWithDefault());
		return Utilities.inputSelect(name, classType, selectedIndustry, industryArray);
	}

	public boolean isOK() throws Exception {
		errorMessages = new Vector<String>();
		if (type == null)
			errorMessages.addElement("Please indicate the account type.");
		if (verifyUsername(username, id))
			errorMessages.addElement("Username already exists. Please type another.");
		if (name == null || name.length() == 0)
			errorMessages.addElement("Please fill in the Company Name field.");
		if (name == null || name.length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long.");
		if ("Contractor".equals(type)) {
			if (username == null || username.length() == 0)
				errorMessages.addElement("Please fill in the Username field.");
			if (password == null || password.length() < MIN_PASSWORD_LENGTH)
				errorMessages.addElement("Please choose a password at least " + MIN_PASSWORD_LENGTH
						+ " characters in length.");
			if (password == null || password.equalsIgnoreCase(username))
				errorMessages.addElement("Please choose a password different from your username.");
		}
		// Don't check these fields if auditor BJ 10-28-04
		if (type == null || !type.equals("Auditor")) {
			if (contact == null || contact.length() == 0)
				errorMessages.addElement("Please fill in the Contact field.");
			if (address == null || address.length() == 0)
				errorMessages.addElement("Please fill in the Address field.");
			if (city == null || city.length() == 0)
				errorMessages.addElement("Please fill in the City field.");
			if (zip == null || zip.length() == 0)
				errorMessages.addElement("Please fill in the Zip field.");
			if (phone == null || phone.length() == 0)
				errorMessages.addElement("Please fill in the Phone field.");
		}
		if ((email == null || email.length() == 0) || (!Utilities.isValidEmail(email)))
			errorMessages
					.addElement("Please enter a valid email address. This is our main way of communicating with you so it must be valid.");
		return (errorMessages.size() == 0);
	}

	// 1/15/05 jj - defrags tables
	public void optimizeDB() throws Exception {
		String optimizeQuery = "OPTIMIZE TABLE OSHA,accounts,auditCategories,auditData,auditQuestions,certificates,contractor_info,"
				+ "forms,generalContractors,loginLog,users;";
		try {
			DBReady();
			SQLStatement.executeUpdate(optimizeQuery);
		} finally {
			DBClose();
		}
	}

	// 4/1/05 bj, called from login, linked from welcome email contractor.
	// Confirms correct email address
	// 6/23/05 jj, updating email also activates account
	public void updateEmailConfirmedDate(String username) throws Exception {
		try {
			DBReady();
			SQLStatement.executeUpdate("UPDATE accounts SET emailConfirmedDate=CURDATE() "
					+ "WHERE username='" + username + "'");
		} finally {
			DBClose();
		}
	}

	public boolean contractorNameExists(String company) throws Exception {
		if (company.length() < 3) {
			errorMessages.addElement("Your company name must be at least 3 characters long");
			return true;
		}
		String selectQuery = "SELECT id FROM accounts WHERE UCASE(name) LIKE UCASE('" + eqDB(company) + "%');";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				SQLResult.close();
				DBClose();
				errorMessages
						.addElement("Someone from your company has already created a PICS account.  Please contact us at: 949.387.1940");
				return true;
			} else {
				SQLResult.close();
				DBClose();
				return false;
			}
		} finally {
			DBClose();
		}
	}

	public String eqDB(String temp) {
		return Utilities.escapeQuotes(temp);
	}

	private String[] filteredGenerals(String[] generals, String id) throws Exception {
		if (id.equals(""))
			return generals;
		List<String> retList = new ArrayList<String>();
		String selectQuery = "SELECT genID FROM generalContractors WHERE subid='" + id + "';";
		ResultSet SQLResult;
		try {
			DBReady();
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				String genID = SQLResult.getString("genID");
				for (int i = 0; i < generals.length; i++) {
					if (genID.equals(generals[i])) {
						retList.add(generals[i]);
						retList.add(generals[i + 1]);
					}
				}
			}
			return (String[]) retList.toArray(new String[0]);
		} finally {
			DBClose();
		}
	}

	public String getName(String id) throws Exception {
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

	public boolean isOperator() {
		return "Operator".equals(this.type);
	}

	public boolean isCorporate() {
		return "Corporate".equals(this.type);
	}

	public boolean isContractor() {
		return "Contractor".equals(this.type);
	}

	public void updateLastLogin() throws SQLException {
		if (id == null || id.equals(""))
			return;

		try {
			DBReady();
			String sql = "UPDATE accounts SET lastLogin=NOW() WHERE id=" + id;
			SQLStatement.executeUpdate(sql);
		} finally {
			DBClose();
		}

	}
}
