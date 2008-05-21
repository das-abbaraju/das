package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;

import com.picsauditing.PICS.pqf.CategoryBean;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.User;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.mail.EmailContractorBean;
import com.picsauditing.mail.EmailTemplates;
import com.picsauditing.util.SpringUtils;

public class ContractorBean extends DataBean {
	//This is so contractors can be removed from the activation report without actually entering a valid login date.
	//It's a hack, but what John wants doesn't make sense, and hopefully this is temporary BJ 2-15-05
	public static final String REMOVE_FROM_REPORT = "1/1/50";
	public static final String[] RISK_LEVEL_ARRAY = {"Low","Med","High"};
	public static final String[] RISK_LEVEL_VALUES_ARRAY = {"1","2","3"};

	public String id = "";
	public String taxID = "";
	public String main_trade = "";
	public String trades = "0;";
	public String subTrades = "0;";
	public String logo_file = "No";
	public String brochure_file = "No";
	public String description = "";
	public boolean isDescriptionChanged = false;
	public String certs = "0";
	public String notes = "";
	public String adminNotes = "";
	public boolean isNotesChanged = false;
	public boolean isAdminNotesChanged = false;
	public String mustPay = "Yes";
	public String welcomeAuditor_id= "0";

	public String requestedByID = "";
	public String billingCycle = "";
	public String billingAmount = "";
	public String isExempt = "No";
	public String hasExpiredCerts = "No";
	public int facilitiesCount = 0;
	public String isOnlyCerts = "No";
	public String setTrades = ""; 

	public String accountDate = ""; // The first time a user logs into this Contractor account
	public String membershipDate = "";
	public String welcomeCallDate = "";
	public String welcomeEmailDate = "";
	public String lastPayment = "";
	public String lastPaymentAmount = "";
	public String paymentExpires = "";
	public String lastInvoiceDate = "";
	public String lastAnnualUpdateEmailDate = "";
	public String riskLevel = "2";
	public int annualUpdateEmails = 0;
	// questionID=894 
	// 22.1.1  	Does your company have employees who are covered under DOT OQ requirements?
	public String oqEmployees = "";
	
	private HashMap<Integer, ContractorAudit> audits;

	int num_of_trades = 0;
	int num_of_subTrades = 0;

	// second contact
	public String secondContact = "";
	public String secondPhone = "";
	public String secondEmail = "";
	// billing contact
	public String billingContact = "";
	public String billingPhone = "";
	public String billingEmail = "";
	public String payingFacilities = "";
	public String newBillingAmount = "";
	
	private User primaryUser = new User();

	private ArrayList<OperatorBean> facilities;
	public ArrayList<String> generalContractors = new ArrayList<String>();
	ArrayList<String> newGeneralContractors = null;
	ArrayList<String> blockedDates = new ArrayList<String>();
	
	public void setId(String s) {id = s;}//setId
	private int getIdInteger() {
		int id = 0;
		try {
			id = Integer.parseInt(this.id);
		} catch (Exception e) {}
		return id;
	}
	public void setMain_trade(String s) {main_trade = s;}//setMain_trade
	public void setTrades(String[] s) {
		if (s==null) {
			num_of_trades = 0;
			trades = "0;";
			return;
		}//if
		num_of_trades = s.length;
		trades = String.valueOf(num_of_trades) + ";";
		for (int i = 1; i <= num_of_trades; i++) {
			trades += (s[i-1] + ";");
		}//for
	}//setTrades
	public void setSubTrades(String[] s) {
		if (s==null) {
			num_of_subTrades = 0;
			subTrades = "0;";
			return;
		}//if
		num_of_subTrades = s.length;
		subTrades = String.valueOf(num_of_subTrades) + ";";
		for (int i = 1; i <= num_of_subTrades; i++) {
			subTrades += (s[i-1] + ";");
		}//for
	}//setSubTrades

	public void setGeneralContractorsFromCheckList(javax.servlet.http.HttpServletRequest request) {
		newGeneralContractors = new ArrayList<String>();
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("genID_") && "Yes".equals(request.getParameter(temp))) {
				String opID = temp.substring(6);
				newGeneralContractors.add(opID);
			}//if
		}//while
	}
	
	public void setGeneralContractorsFromStringArray(String[] s) {
		newGeneralContractors = new ArrayList<String>();
		if (s != null) {
			int num = s.length;
			for (int i = 1; i <= num; i++) {
				newGeneralContractors.add(s[i-1]);
			}//for
		}//if
		generalContractors = newGeneralContractors;
	}
	
	public ArrayList<OperatorBean> getFacilities() throws Exception {
		if (this.facilities == null) {
			OperatorBean oBean = new OperatorBean();
			facilities = oBean.getListByWhere("id IN (SELECT genID FROM generalContractors WHERE subID = '"+Utilities.intToDB(this.id)+"')");
		}
		return facilities;
	}
	public void setFacilities(ArrayList<String> newFacilities) throws Exception {
		String sqlList = "0";
		for(String opID : newFacilities)
			sqlList += "," + Utilities.intToDB(opID);
		facilities = new ArrayList<OperatorBean>();
		OperatorBean oBean = new OperatorBean();
		facilities = oBean.getListByWhere("id IN ("+sqlList+")");
	}
	
	public String getLuhnId() {
		return com.picsauditing.util.Luhn.addCheckDigit(id);
	}

	public void setTradeString(String s) {trades = s;}//setTradeString
	public void setSubTradeString(String s) {subTrades = s;}//setSubTradeString
	public void setLogo_file(String s) {logo_file = s;}//setLogo_file
	public void setBrochure_file(String s) {brochure_file = s;}//setBrochure_file
	public void setDescription(String s) {
		if (s.length() != description.length() || !s.equals(description)){
			description = s;
			isDescriptionChanged = true;
		}
	}
	public void setAccountDate(String s) {accountDate = s;}//setAccountDate
	public void setWelcomeEmailDate(String s) {welcomeEmailDate = s;}//setWelcomeEmailDate
	public void setLastPayment(String s) {lastPayment = s;}//setLastPayment
	public void setNotes(String s) {notes = s;}//setNotes
	public void setMustPay(String s) {mustPay = s;}//setMustPay
	public void setPaymentExpires(String s) {paymentExpires = s;}//setPaymentExpires
	public String getId() {return id;}//getId
	public String getMain_trade() {return main_trade;}//getMain_trade
	public String[] getTrades() {
		String[] temp = new String[num_of_trades];
		int i1 = 0;
		int i2 = trades.indexOf(";") + 1;
		for (int i = 1; i <= num_of_trades; i++) {
			i1 = trades.indexOf(";", i2);
			temp[i-1] = trades.substring(i2, i1);
			i2 = i1+1;
		}//for
		return temp;
	}//getTrades
	public String[] getSubTrades() {
		String[] temp = new String[num_of_subTrades];
		int i1 = 0;
		int i2 = trades.indexOf(";") + 1;
		for (int i = 1; i <= num_of_subTrades; i++) {
			i1 = subTrades.indexOf(";", i2);
			temp[i-1] = subTrades.substring(i2, i1);
			i2 = i1+1;
		}//for
		return temp;
	}//getSubTrades
	public String[] getGeneralContractorsArray() {
		return (String[])generalContractors.toArray(new String[0]);
	}//getGeneralContractorsArray
	public String getDisplayLogo_file() {
		if ("No".equals(logo_file))	return "logo_default.gif";
		else	return logo_file;
	}//getDisplayLogo_file
	public String getDescriptionHTML() {return Utilities.escapeNewLines(description);}//getDescriptionHTML
	public String getAccountDate() {
		if (REMOVE_FROM_REPORT.equals(accountDate))
			return "";
		else
			return accountDate;}//getAccountDate
	public String getNotesHTML() {return Utilities.escapeHTML(notes);}//getNotesHTM
	public String getRiskLevelShow() {
		try {
			return RISK_LEVEL_ARRAY[Integer.parseInt(riskLevel)-1];
		} catch (Exception e) {
			return "Unknown";
		}
	}

	public boolean isCertRequired() throws Exception {
		setFacilitiesFromDB();
		if (0==facilitiesCount)
			return false;
		ListIterator<String> li = generalContractors.listIterator();
		OperatorBean tempOBean = new OperatorBean();
		while (li.hasNext()) {
			String opID = (String)li.next();
			tempOBean.setFromDB(opID);
			if (tempOBean.canSeeInsurance())
				return true; 
		}//while
		return false;
	}

	public String getTradesList() {
		return trades.substring(trades.indexOf(";")+1,trades.length()-1).replaceAll(";",", ");
	}
	
	public String getSubTradesList() {
		if (subTrades.substring(0,1).equals("0"))
			return "";
		return subTrades.substring(subTrades.indexOf(";")+1,subTrades.length()-1).replaceAll(";",", ");
	}
	
	public int getNumOfTrades() {return num_of_trades;}//getNumOfTrades
	public int getNumOfSubTrades() {return num_of_subTrades;}//getNumOfSubTrades
	public boolean isTrade(String s) {return (trades.indexOf(s) != -1);}//isTrade
	public boolean isSubTrade(String s) {return (subTrades.indexOf(s) != -1);}//isSubTrade
	public String getIsLogoFile() {
		if ("No".equals(logo_file))	return "";
		else	return "<font color=red>*</font>";
	}//getIsLogoFile
	public String getIsBrochureFile() {
		if ("No".equals(brochure_file))	return "";
		else	return "<font color=red>*</font>";
	}//getIsBrochureFile

	public void setFromDB(String conID) throws Exception {
		id = conID;
		setFromDB();
	}

	public void setFromDB() throws Exception {
		try{
			if ((null == id) || ("".equals(id)))
				throw new Exception("can't set contractor info from DB because id is not set");
			DBReady();
			String Query = "SELECT * FROM contractor_info WHERE id="+id;
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
			setFacilitiesFromDB();
		}finally{
			DBClose();
		}
	}

	public void setFacilitiesFromDB() throws Exception {
		// set the sub/generalContractors from the generalContractors table
		if ((null == id) || ("".equals(id)))
			throw new Exception("can't set contractor info from DB because id is not set");
		try{
			DBReady();
			String selectQuery = "SELECT genID FROM generalContractors WHERE subID='"+id+"';";
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			facilitiesCount = 0;
			generalContractors.clear();
			while (SQLResult.next()) {
				generalContractors.add(SQLResult.getString("genID"));
				facilitiesCount++;
			}
			SQLResult.close();
		}finally{
			DBClose();
		}
	}

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		id = SQLResult.getString("id");
		taxID = SQLResult.getString("taxID");
		main_trade = SQLResult.getString("main_trade");
		trades = SQLResult.getString("trades");
		subTrades = SQLResult.getString("subTrades");
		logo_file = SQLResult.getString("logo_file");
		welcomeAuditor_id = SQLResult.getString("welcomeAuditor_id");
		brochure_file = SQLResult.getString("brochure_file");
		//fix ms word apostrophes changed to ?
		description = SQLResult.getString("description");
		description = description.replace('?','\'');
		//}
		certs = SQLResult.getString("certs");

		notes = SQLResult.getString("notes");
		adminNotes = SQLResult.getString("adminNotes");
		mustPay = SQLResult.getString("mustPay");
		requestedByID = SQLResult.getString("requestedByID");
		billingAmount = SQLResult.getString("billingAmount");
		billingCycle = SQLResult.getString("billingCycle");
		isExempt = SQLResult.getString("isExempt");
		hasExpiredCerts = SQLResult.getString("hasExpiredCerts");
		isOnlyCerts = SQLResult.getString("isOnlyCerts");

		accountDate = DateBean.toShowFormat(SQLResult.getString("accountDate"));
		membershipDate = DateBean.toShowFormat(SQLResult.getString("membershipDate"));
		welcomeCallDate = DateBean.toShowFormat(SQLResult.getString("welcomeCallDate"));
		welcomeEmailDate = DateBean.toShowFormat(SQLResult.getString("welcomeEmailDate"));
		lastPayment = DateBean.toShowFormat(SQLResult.getString("lastPayment"));
		lastPaymentAmount = SQLResult.getString("lastPaymentAmount");
		
		paymentExpires = DateBean.toShowFormat(SQLResult.getString("paymentExpires"));
		lastInvoiceDate = DateBean.toShowFormat(SQLResult.getString("lastInvoiceDate"));
		lastAnnualUpdateEmailDate = DateBean.toShowFormat(SQLResult.getString("lastAnnualUpdateEmailDate"));
		
//second contact
		secondContact = SQLResult.getString("secondContact");
		secondPhone = SQLResult.getString("secondPhone");
		secondEmail = SQLResult.getString("secondEmail");
//billing contact		
		billingContact = SQLResult.getString("billingContact");
		billingPhone = SQLResult.getString("billingPhone");
		billingEmail = SQLResult.getString("billingEmail");
		payingFacilities = SQLResult.getString("payingFacilities");
		newBillingAmount = SQLResult.getString("newBillingAmount");

		riskLevel = SQLResult.getString("riskLevel");
		annualUpdateEmails = SQLResult.getInt("annualUpdateEmails");
		oqEmployees = SQLResult.getString("oqEmployees");
	}

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE contractor_info SET "+
			"taxID='"+eqDB(taxID)+
			"',main_trade='"+main_trade+
			"',trades='"+ trades+
			"',subTrades='"+subTrades+ 
			"',logo_file='"+logo_file+
			"',brochure_file='"+brochure_file+
			"',certs='"+certs+
			"',mustPay='"+mustPay+
			"',welcomeAuditor_id='"+welcomeAuditor_id+
			"',requestedByID='"+requestedByID+
			"',billingAmount='"+billingAmount+
			"',billingCycle='"+billingCycle+
			"',hasExpiredCerts='"+hasExpiredCerts+
			"',isOnlyCerts='"+isOnlyCerts+

			"',accountDate='"+DateBean.toDBFormat(accountDate)+
			"',membershipDate='"+DateBean.toDBFormat(membershipDate)+
			"',welcomeCallDate='"+DateBean.toDBFormat(welcomeCallDate)+
			"',welcomeEmailDate='"+DateBean.toDBFormat(welcomeEmailDate)+
			"',lastPayment='"+DateBean.toDBFormat(lastPayment) +
			"',lastPaymentAmount='"+lastPaymentAmount+
			"',paymentExpires='"+DateBean.toDBFormat(paymentExpires)+
			"',lastInvoiceDate='"+DateBean.toDBFormat(lastInvoiceDate)+
			"',lastAnnualUpdateEmailDate='"+DateBean.toDBFormat(lastAnnualUpdateEmailDate)+
			"',secondContact='"+eqDB(secondContact)+
			"',secondPhone='"+eqDB(secondPhone)+
			"',secondEmail='"+eqDB(secondEmail)+
			"',billingContact='"+eqDB(billingContact)+
			"',billingPhone='"+eqDB(billingPhone)+
			"',riskLevel="+riskLevel+
			",annualUpdateEmails="+annualUpdateEmails+
			",oqEmployees='"+eqDB(oqEmployees)+
			"',billingEmail='"+eqDB(billingEmail);
		if (isDescriptionChanged)
			updateQuery+="',description='"+eqDB(description);
		if (isNotesChanged)
			updateQuery+="',notes='"+eqDB(notes);
		if (isAdminNotesChanged)
			updateQuery+="',adminNotes='"+eqDB(adminNotes);
		updateQuery+="' WHERE id="+id+";";
		try {
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}
	}

	public void writeNewToDB(Facilities FACILITIES) throws Exception {
		try {
			DBReady();
			String Query = "INSERT INTO contractor_info (id) VALUES ('"+id+"');";
			SQLStatement.executeUpdate(Query);
			DBClose();
			writeToDB();
			
			DBReady();
			String insertQuery = "INSERT INTO generalContractors (subID,genID,dateAdded) VALUES ";
			boolean doInsert = false;
			for (String genID: newGeneralContractors) {
				doInsert = true;
				insertQuery += "("+id+","+genID+",NOW()),";
				addNote(id, "","Added this Contractor to "+FACILITIES.getNameFromID(genID)+"'s db at account registration", DateBean.getTodaysDateTime());
			}
			insertQuery = insertQuery.substring(0,insertQuery.length()-1) + ";";
			if (doInsert)
				SQLStatement.executeUpdate(insertQuery);
			DBClose();
			writeToDB();
			
			DBReady();
			
			BillContractor billing = new BillContractor();
			billing.setContractor(this);
			billing.calculatePrice();
			billing.writeToDB();
			com.picsauditing.PICS.OperatorBean.resetSubCountTable();
		}finally{
			DBClose();
		}
}
	
	public void writeBillingToDB() throws Exception {
		try {
			DBReady();
			String updateQuery = "UPDATE contractor_info SET payingFacilities="+Utilities.intToDB(this.payingFacilities)+
					", newBillingAmount="+Utilities.intToDB(this.newBillingAmount)+", isExempt='"+eqDB(isExempt)+"' " +
					"WHERE id="+Utilities.intToDB(this.id);
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}
	}

	public void setFromUploadRequest(HttpServletRequest r) throws Exception {
		Map<String,String> m = (Map<String,String>)r.getAttribute("uploadfields");
		taxID = m.get("taxID");
		main_trade = m.get("main_trade");
		setDescription(m.get("description"));
		mustPay = m.get("mustPay");
		paymentExpires = m.get("paymentExpires");
		lastPayment = m.get("lastPayment");
		lastPaymentAmount = m.get("lastPaymentAmount");
		
		membershipDate = m.get("membershipDate");
		lastInvoiceDate = m.get("lastInvoiceDate");
		requestedByID = m.get("requestedByID");
		billingAmount = m.get("billingAmount");
		billingCycle = m.get("billingCycle");
		// We only set this via the BillingContractor class now
		//isExempt = m.get("isExempt");
		isOnlyCerts = m.get("isOnlyCerts");
		setTrades = m.get("trades");
//		second contact
		secondContact = m.get("secondContact");
		secondEmail = m.get("secondEmail");
		secondPhone = m.get("secondPhone");
//billing contact
		billingContact = m.get("billingContact");
		billingEmail = m.get("billingEmail");
		billingPhone = m.get("billingPhone");

		riskLevel = m.get("riskLevel");
		oqEmployees = m.get("oqEmployees");

		//setTrades(m.getValues("trades"));
// jj 10/28/06	setGeneralContractors(m.getValues("generalContractors"));
		
		if (num_of_trades == 0){
			num_of_trades++;
			trades = "1;" + main_trade + ";";
		}//if

// Set the files from the db
		if (!"".equals(id)){
			String selectQuery = "SELECT logo_file,brochure_file FROM contractor_info WHERE id="+id+";";
			try {
				DBReady();
				ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
				if (SQLResult.next()){
					logo_file = SQLResult.getString("logo_file");
					brochure_file = SQLResult.getString("brochure_file");
				}else{
					logo_file = "No";
					brochure_file = "No";
				}//else
				SQLResult.close();
			}finally{
				DBClose();
			}//finally
		}//if
	}//setFromUploadRequest
	
	public void setFromUploadRequestClientNew(javax.servlet.http.HttpServletRequest r) throws Exception {
		taxID = r.getParameter("taxID");
		description = r.getParameter("description");
		isDescriptionChanged = true;
		main_trade = r.getParameter("main_trade");
		setGeneralContractorsFromStringArray(r.getParameterValues("generalContractors"));
		requestedByID = r.getParameter("requestedByID");
		secondContact = r.getParameter("secondContact");
		secondEmail = r.getParameter("secondEmail");
		secondPhone = r.getParameter("secondPhone");
		billingContact = r.getParameter("billingContact");
		billingEmail = r.getParameter("billingEmail");
		billingPhone = r.getParameter("billingPhone");
		oqEmployees = r.getParameter("oqEmployees");
		riskLevel = r.getParameter("riskLevel");
	}

	public void setFromUploadRequestClientEdit(javax.servlet.http.HttpServletRequest r) throws Exception {
		Map<String,String> m = (Map<String,String>)r.getAttribute("uploadfields");
		main_trade = m.get("main_trade");
		secondContact = m.get("secondContact");
		secondEmail = m.get("secondEmail");
		secondPhone = m.get("secondPhone");
		billingContact = m.get("billingContact");
		billingEmail = m.get("billingEmail");
		billingPhone = m.get("billingPhone");
		setDescription(m.get("description"));
	}

	public boolean isOK() {
		errorMessages = new Vector<String>();		
		if (num_of_trades == 0) {
			num_of_trades++;
			trades = "1;" + main_trade + ";";
		}//if
		if (main_trade.equals(TradesBean.DEFAULT_SELECT_TRADE))
			errorMessages.addElement("Please select a main trade");
		if (requestedByID.length() == 0)
			errorMessages.addElement("Please select a choice for the Audit Requested By field");
		return (errorMessages.size() == 0);
	}//isOK

	public boolean isOKClientCreate() throws Exception {
		errorMessages = new Vector<String>();
		if (!java.util.regex.Pattern.matches("\\d{9}", taxID))
			errorMessages.addElement("Pleae enter your 9 digit tax ID with only digits 0-9, no dashes.");
		else if (taxIDExists(taxID))
			errorMessages.addElement("The tax ID <b>"+taxID+"</b> already exists.  Please contact a company representative.");
		if (num_of_trades == 0) {
			num_of_trades++;
			trades = "1;"+main_trade+";";
		}//if
		if (main_trade.equals(TradesBean.DEFAULT_SELECT_TRADE))
			errorMessages.addElement("Please select a main trade");
		if (requestedByID == null || requestedByID.length() == 0)
			errorMessages.addElement("Please select a choice for the Audit Requested By field");
		return (errorMessages.size() == 0);
	}//isOKClientCreate

	public User getPrimaryUser() {
		return this.primaryUser;
	}
	public String getUsername() throws Exception {
		if (primaryUser.userDO.id.length() == 0) primaryUser.setFromAccountID(this.id);
		return primaryUser.userDO.username;
	}
	
	public String getPassword() throws Exception {
		if (primaryUser.userDO.id.length() == 0) primaryUser.setFromAccountID(this.id);
		return primaryUser.userDO.password;
	}
	
	public String getLastLogin() throws Exception {
		if (primaryUser.userDO.id.length() == 0) primaryUser.setFromAccountID(this.id);
		return primaryUser.userDO.lastLogin;
	}
	
	public boolean taxIDExists(String tID) throws Exception {
		String selectQuery = "SELECT id FROM contractor_info WHERE taxID='"+tID+"';";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				SQLResult.close();
				DBClose();
				return true;
			} else {
				SQLResult.close();
				DBClose();
				return false;
			}//else
		}finally{
			DBClose();
		}//finally
	}//taxIDExists
	
	public boolean writeGeneralContractorsToDB(PermissionsBean pBean,Facilities FACILITIES) throws Exception {
		try {
			DBReady();
			ListIterator<String> li = generalContractors.listIterator();
			while (li.hasNext()) {
				String oldGen = (String)li.next();
				if (!newGeneralContractors.contains(oldGen)) {
					String deleteQuery = "DELETE FROM generalContractors WHERE subID="+id+" AND genID="+oldGen+" LIMIT 1;";
					SQLStatement.executeUpdate(deleteQuery);
				}//if
			}//while
			ArrayList<String> toAdd = new ArrayList<String>();
			li = newGeneralContractors.listIterator();
			while (li.hasNext()) {
				String newGen = (String)li.next();
				if (!generalContractors.contains(newGen))
					toAdd.add(newGen);
			}//while
			if (!toAdd.isEmpty()) {
				String replaceQuery = "REPLACE INTO generalContractors (subID,genID,dateAdded) VALUES ";
				li = toAdd.listIterator();
				while (li.hasNext()) {
					String genID = (String)li.next();
					replaceQuery += "("+id+","+genID+",NOW()),";
					addNote(id,pBean.getPermissions().getUsername(), "Added this Contractor to "+FACILITIES.getNameFromID(genID)+"'s db", DateBean.getTodaysDateTime());
				}//while
				replaceQuery = replaceQuery.substring(0,replaceQuery.length()-1) + ";";
				SQLStatement.executeUpdate(replaceQuery);
			}//if
			DBClose();
			com.picsauditing.PICS.OperatorBean.resetSubCountTable();
			return !toAdd.isEmpty();
		}finally{
			DBClose();
		}//finally
	}//writeGeneralContractorsToDB

	public void addNote(String conID, String pre, String newNote, String notesDate) throws Exception {
		notes = notesDate+" "+pre+": "+newNote+"\n"+notes;
		isNotesChanged = true;
	}

	public void addAdminNote(String conID, String pre, String newNote, String notesDate) throws Exception {
		adminNotes = notesDate+" "+pre+": "+newNote+"\n"+adminNotes;
		isAdminNotesChanged = true;
	}

	//Jeff  2/2/05
	//Records a payment by a contrator, invoked on Schedule Audits report
	//3/19/05 jj - added paymentExpires calculations
	public void updateLastPayment(String id, String adminName, String amount) throws Exception {
		setFromDB(id);
		Calendar newPaymentExpiresCal = Calendar.getInstance();
		SimpleDateFormat showFormat = new SimpleDateFormat("M/d/yy");
		if (!"".equals(paymentExpires)){
			newPaymentExpiresCal.setTime(showFormat.parse(paymentExpires));
			newPaymentExpiresCal.add(Calendar.YEAR, 1);
		}else if (!"".equals(membershipDate)){
			newPaymentExpiresCal.setTime(showFormat.parse(membershipDate));
			while (newPaymentExpiresCal.before(Calendar.getInstance()))
				newPaymentExpiresCal.add(Calendar.YEAR, 1);
		}else{
			membershipDate = showFormat.format(newPaymentExpiresCal.getTime());
			newPaymentExpiresCal.add(Calendar.YEAR, 1);		
			addAdminNote(id, "("+adminName+")", "Membership date set today", membershipDate);
		}//else
		paymentExpires = showFormat.format(newPaymentExpiresCal.getTime());
		lastPayment = DateBean.getTodaysDate();
		lastPaymentAmount = amount;
		addAdminNote(id, "("+adminName+")", "Payment received for $"+amount+" for "+payingFacilities+" facilities", lastPayment);
		writeToDB();
	}//updateLastPayment

	public void upgradePayment(String id, String adminName, String newAmount) throws Exception {
		setFromDB(id);
		lastPayment = DateBean.getTodaysDate();
		addAdminNote(id, "("+adminName+")", "Payment upgraded from $"+lastPaymentAmount+" to $"+newAmount+" for "+payingFacilities+" facilities", lastPayment);
		lastPaymentAmount = newAmount;
		writeToDB();
	}

	@Deprecated
	public boolean isExempt() {
		return isAudited();
	}
	
	@Deprecated
	public boolean isAudited() {
		// We should check the contractor's audits instead
		return "Yes".equals(this.isExempt);
	}
	
	@Deprecated
	public void isAudited(boolean value) {
		if (value)
			this.isExempt = "No";
		else
			this.isExempt = "Yes";
	}

	public void convertTrades() throws Exception {
		try {
			com.picsauditing.PICS.TradesBean tBean = new com.picsauditing.PICS.TradesBean();
			tBean.setFromDB();
			ArrayList<String> tradesAL = tBean.trades;
			String Query = "SELECT * FROM contractor_info;";
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			while (SQLResult.next()) {
				setFromResultSet(SQLResult);
				ListIterator<String> li = tradesAL.listIterator();
				String insertQuery = "REPLACE INTO pqfData (conID,questionID,answer) VALUES ";
				boolean insert = false;
				while (li.hasNext()) {
					String qID = (String)li.next();
					String trade = (String)li.next();
					String answer="";
					if (-1 != trades.indexOf(trade))
						answer+="C";
					if (-1 != subTrades.indexOf(trade))
						answer+="S";
					if (!"".equals(answer)) {
						insertQuery+="("+id+","+qID+",'"+answer+"'),";
						insert = true;
					}//if
				}//while
				insertQuery = insertQuery.substring(0,insertQuery.length()-1);
				insertQuery +=";";
				if (insert)
					SQLStatement.executeUpdate(insertQuery);
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//convertTrades

	public void setUploadedFiles(HttpServletRequest request){
		String fn = (String)request.getAttribute("logo_file");
		if(fn != null)
			logo_file = FilenameUtils.getName(fn);
		fn = (String)request.getAttribute("brochure_file");
		if(fn != null)
			brochure_file = FilenameUtils.getName(fn);
	}
	
	public void tryView(Permissions permissions) throws NoRightsException {
		if (canView(permissions, "summary")) return;
		throw new NoRightsException("Contractor");
	}
	public boolean canView(Permissions permissions) {
		return canView(permissions, "summary");
	}
	public boolean canView(Permissions permissions, String what) {
		if (permissions.hasPermission(OpPerms.AllContractors)) return true;
		
		// OR
		if (permissions.isContractor()) {
			return permissions.getAccountIdString().equals(this.id);
		}
		
		if (permissions.isOperator() || permissions.isCorporate()) {
			// I don't really like this way. It's a bit confusing
			// Basically, if all we're doing is searching for contractors
			// and looking at their summary page, then it's OK
			// If we want to look at their detail, like PQF data
			// Then we have to add them first (generalContractors).
			if ("summary".equals(what)) {
				// Until we figure out Contractor viewing permissions better, this will have to do
				return true;
			}
			if (permissions.isCorporate()) {
				OperatorBean operator = new OperatorBean();
				try {
					operator.isCorporate = true;
					operator.setFromDB(permissions.getAccountIdString());
					// if any of this corporate operators can see this contractor, 
					// then the corporate users can see them too
					for (String id : operator.facilitiesAL) {
						if (generalContractors.contains(id))
							return true;
					}
				} catch (Exception e) {}
				return false;
			}
			// To see anything other than the summary, you need to be on their list
			return generalContractors.contains(permissions.getAccountIdString());
		}
		
		// The auditors can see this Contractor
		for(ContractorAudit audit : getAudits())
			if (audit.getAuditor().getId() == permissions.getUserId()) return true;

		return false;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public HashMap<Integer, ContractorAudit> getValidAudits() {
		if (audits == null) {
			ContractorAuditDAO dao = (ContractorAuditDAO)SpringUtils.getBean("ContractorAuditDAO");
			// Get list of PQF, Office, Desktop, DA that are in the Verified or Exempt status
			List<ContractorAudit> auditList = dao.findNonExpiredByContractor(Integer.parseInt(id));
			audits = new HashMap<Integer, ContractorAudit>();
			for(ContractorAudit cAudit : auditList) {
				if (!cAudit.getAuditType().isHasMultiple())
					audits.put(cAudit.getAuditType().getAuditTypeID(), cAudit);
			}
		}
		return audits;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ContractorAudit> getAudits() {
		ContractorAuditDAO dao = (ContractorAuditDAO)SpringUtils.getBean("ContractorAuditDAO");
		return dao.findByContractor(Integer.parseInt(id));
	}

	public ContractorAudit getAudit(int auditTypeID) {
		if (audits == null)
			getValidAudits();
		return audits.get(auditTypeID);
	}

	public void buildAudits() throws Exception {
		// Throwing an exception here may be more appropriate
		if (getIdInteger() == 0) {
			AuditBuilder auditBuilder = (AuditBuilder)SpringUtils.getBean("AuditBuilder");
			auditBuilder.buildAudits(getIdInteger());
		}
	}
}