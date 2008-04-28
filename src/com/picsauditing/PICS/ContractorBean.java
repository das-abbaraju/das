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
	public static final String STATUS_ACTIVE = "Active"; // these should match the ENUM for status in the DB table
	public static final String STATUS_INACTIVE = "Inactive"; // these should match the ENUM for status in the DB table
	static final String[] STATUS_ARRAY = {STATUS_ACTIVE,STATUS_INACTIVE};
	public static final String AUDIT_STATUS_EXEMPT = "Exempt";
	public static final String AUDIT_STATUS_NOT_SUBMITTED = "Not Submitted";
	public static final String AUDIT_STATUS_VERIFICATION_PENDING= "Verification Pending";
	public static final String AUDIT_STATUS_PQF_PENDING = "PQF Pending";
	public static final String AUDIT_STATUS_PQF_INCOMPLETE = "PQF Incomplete";
	public static final String AUDIT_STATUS_SCHEDULING = "Scheduling";
	public static final String AUDIT_STATUS_SCHEDULED = "Scheduled";
	public static final String AUDIT_STATUS_RQS = "RQs Pending";
	public static final String AUDIT_STATUS_CLOSED = "Closed";
	public static final String AUDIT_STATUS_EXPIRED = "Expired";
	static final String[] AUDIT_STATUS_ARRAY = {AUDIT_STATUS_EXEMPT,AUDIT_STATUS_PQF_PENDING,AUDIT_STATUS_PQF_INCOMPLETE,AUDIT_STATUS_SCHEDULING,AUDIT_STATUS_SCHEDULED,
												AUDIT_STATUS_RQS,AUDIT_STATUS_CLOSED};
	//This is so contractors can be removed from the activation report without actually entering a valid login date.
	//It's a hack, but what John wants doesn't make sense, and hopefully this is temporary BJ 2-15-05
	public static final String REMOVE_FROM_REPORT = "1/1/50";
	public static final boolean SHOW_AUDIT_DATE = true; //MULTIAUDIT remove
	public static final boolean DONT_SHOW_AUDIT_DATE = false; //MULTIAUDIT remove
	public static final String[] AUDIT_LOCATION_ARRAY = {"On Site","Web"};	// must match ENUM in db, contractor_info.auditLocation 
	public static final int SETUP_FEE = 99;
	public static final String[] RISK_LEVEL_ARRAY = {"Low","Med","Med-High"};
	public static final String[] RISK_LEVEL_VALUES_ARRAY = {"1","2","3"};

	public String id = "";
	public String taxID = "";
	public String main_trade = "";
	public String trades = "0;";
	public String subTrades = "0;";
	public String logo_file = "No";
	public String auditHour = ""; //MULTIAUDIT remove
	public String auditAmPm = ""; //MULTIAUDIT remove
	public String brochure_file = "No";
	public String description = "";
	public boolean isDescriptionChanged = false;
	//public String status = STATUS_INACTIVE;
	public String certs = "0";
	public String notes = "";
	public String adminNotes = "";
	public boolean isNotesChanged = false;
	public boolean isAdminNotesChanged = false;
	public String canEditPrequal = "Yes"; //MULTIAUDIT remove
	public String canEditDesktop = "Yes"; //MULTIAUDIT remove
	public String mustPay = "Yes";
	public String auditor_id= "0"; //MULTIAUDIT remove
	public String welcomeAuditor_id= "0";
	public String desktopAuditor_id = "0"; //MULTIAUDIT remove
	public String daAuditor_id = "0"; //MULTIAUDIT remove
	public String pqfAuditor_id = "0"; //MULTIAUDIT remove
	//public String auditStatus = ""; //MULTIAUDIT remove?

	public String requestedByID = "";
	public String billingCycle = "";
	public String billingAmount = "";
	public String isExempt = "No";
	public String hasExpiredCerts = "No";
	public int facilitiesCount = 0;
	public String isOnlyCerts = "No";
	public String auditLocation= "On Site"; //MULTIAUDIT remove
	public String setTrades = ""; 
//45

	public String pqfPercent = ""; //MULTIAUDIT remove
	public String desktopPercent = ""; //MULTIAUDIT remove
	public String desktopVerifiedPercent = ""; //MULTIAUDIT remove
	public String officePercent = ""; //MULTIAUDIT remove
	public String officeVerifiedPercent = ""; //MULTIAUDIT remove
	public String daPercent = ""; //MULTIAUDIT remove
	public String daVerifiedPercent = ""; //MULTIAUDIT remove
	public String hasNCMSDesktop = "No"; //MULTIAUDIT remove
	public String isNewOfficeAudit = "No"; //MULTIAUDIT remove

//	Dates
	public String pqfSubmittedDate = ""; //MULTIAUDIT remove

	public String desktopAssignedDate = ""; //MULTIAUDIT remove
	public String desktopSubmittedDate = ""; //MULTIAUDIT remove
	public String desktopCompletedDate = ""; //MULTIAUDIT remove
	public String desktopClosedDate = ""; //MULTIAUDIT remove
	public String desktopValidUntilDate = ""; //MULTIAUDIT remove

	public String daAssignedDate = ""; //MULTIAUDIT remove
	public String daSubmittedDate = ""; //MULTIAUDIT remove
	public String daClosedDate = ""; //MULTIAUDIT remove
	public String daRequired = ""; //MULTIAUDIT remove
	
	public String assignedDate = ""; //MULTIAUDIT remove
	public String auditDate = ""; //MULTIAUDIT remove
	public String lastAuditDate = ""; //MULTIAUDIT remove
	public String auditCompletedDate = ""; //MULTIAUDIT remove
	public String auditClosedDate = ""; //MULTIAUDIT remove
	public String auditValidUntilDate = ""; //MULTIAUDIT remove
	public String officeSubmittedDate = ""; //MULTIAUDIT remove
	public String officeClosedDate = ""; //MULTIAUDIT remove

	public String accountDate = ""; // The first time a user logs into this Contractor account
	public String membershipDate = "";
	public String welcomeCallDate = "";
	public String welcomeEmailDate = "";
	public String lastPayment = "";
	public String lastPaymentAmount = "";
	public String paymentExpires = "";
	public String lastAuditEmailDate = ""; //MULTIAUDIT remove
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

//	second contact
	public String secondContact = "";
	public String secondPhone = "";
	public String secondEmail = "";
//billing contact	
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
	public void setAuditDate(String s) {auditDate = s;}//setAuditDate  //MULTIAUDIT remove
	//public void setAuditTime(String s) {auditTime = s;}//setAuditTime  //MULTIAUDIT remove 
	public void setAuditHour(String s) {auditHour = s;}//setAuditHour  //MULTIAUDIT remove
	public void setAuditAmPm(String s) {auditAmPm = s;}//setAuditAmPm  //MULTIAUDIT remove
	public void setAuditor(String s) {auditor_id = s;}//setAuditor  //MULTIAUDIT remove
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
	public void setCanEditPrequal(String s) {canEditPrequal = s;}//setCanEditPrequal
	public void setLastAuditEmailDate(String s) {lastAuditEmailDate = s;}//setLastAuditEmailDate  //MULTIAUDIT remove
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
	public String getAuditClosedDateShow() { //MULTIAUDIT remove
		if ("".equals(auditClosedDate))
			return "No";
		return auditClosedDate;
	}//getAuditClosedDateShow
	public String getAuditCompletedDateShow() {  //MULTIAUDIT remove
		if ("".equals(auditCompletedDate))
			return "No";
		return auditCompletedDate;
	}//getRQClosedTitleDate
	public String[] getGeneralContractorsArray() {
		return (String[])generalContractors.toArray(new String[0]);
	}//getGeneralContractorsArray
	public String getDisplayLogo_file() {
		if ("No".equals(logo_file))	return "logo_default.gif";
		else	return logo_file;
	}//getDisplayLogo_file
	public String getAuditTime() {return auditHour + " " + auditAmPm;}//getAuditTime //MULTIAUDIT remove
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
	public boolean canEditPrequal() {return "Yes".equals(canEditPrequal);}//canEditPrequal //MULTIAUDIT remove
	public boolean canEditDesktop() {return "Yes".equals(canEditDesktop);}//canEditDesktop //MULTIAUDIT remove

	public boolean isAuditCompleted() { //MULTIAUDIT remove
		return !"".equals(auditCompletedDate);
	}//isAuditCompleted

	public boolean isAuditClosed() { //MULTIAUDIT remove
		return !"".equals(auditClosedDate);
	}

	public boolean isDesktopSubmitted() { //MULTIAUDIT remove
		return !"".equals(desktopSubmittedDate);
	}
	public boolean isDesktopCompleted() { //MULTIAUDIT remove
		return !"".equals(desktopCompletedDate);
	}
	public boolean isDesktopClosed() throws Exception{ //MULTIAUDIT remove
		return !("".equals(desktopClosedDate) ||
				DateBean.isFirstBeforeSecond(desktopClosedDate, desktopSubmittedDate));
	}

	public boolean isDaSubmitted() { //MULTIAUDIT remove
		return !"".equals(daSubmittedDate);
	}
	public boolean isDaClosed() throws Exception{ //MULTIAUDIT remove
		return !("".equals(daClosedDate) ||
				DateBean.isFirstBeforeSecond(daClosedDate, daSubmittedDate));
	}

	public boolean isPQFSubmitted() { //MULTIAUDIT remove
		return !"".equals(pqfSubmittedDate);
	}//isPQFSubmitted

	public boolean isOfficeSubmitted() { //MULTIAUDIT remove
		return !"".equals(officeSubmittedDate);
	}//isOfficeSubmitted

	public boolean isOfficeClosed() { //MULTIAUDIT remove
		return !"".equals(officeClosedDate);
	}//isOfficeClosed

	public boolean isNewOfficeAudit() { //MULTIAUDIT remove
		return "Yes".equals(isNewOfficeAudit);
	}//isNewOfficeAudit

	public String calcPICSStatus() throws Exception {
		 //MULTIAUDIT change function calls to get individual audit status
		if ("Yes".equals(isOnlyCerts))
			if ("Yes".equals(hasExpiredCerts))
				return "Inactive";
			else
				return "Active";
		String tempAuditStatus = calcAuditStatus();
		if (AUDIT_STATUS_EXEMPT.equals(tempAuditStatus) || AUDIT_STATUS_CLOSED.equals(tempAuditStatus))
			return "Active";
		return "Inactive";
	}//calcPICSStatus

	public String calcPICSStatusForOperator(OperatorBean oBean) throws Exception {
		 //MULTIAUDIT change function calls to get individual audit status
		if (oBean.isAuditRequired(AuditType.PQF,Integer.parseInt(riskLevel)) && !AUDIT_STATUS_CLOSED.equals(calcPQFStatus()))
			return STATUS_INACTIVE;
		if (isExempt())
			return STATUS_ACTIVE;
		String officeStatus = calcOfficeStatus();
		if (isNewOfficeAudit())
			officeStatus = calcOfficeStatusNew();
		if (oBean.isAuditRequired(AuditType.DESKTOP,Integer.parseInt(riskLevel)))
			if (isDesktopStatusOldAuditStatus()){
				if (!AUDIT_STATUS_CLOSED.equals(officeStatus))
					return STATUS_INACTIVE;
			}//if
			else
				if (!AUDIT_STATUS_CLOSED.equals(calcDesktopStatus()))
					return STATUS_INACTIVE;
		if (oBean.isAuditRequired(AuditType.DA,Integer.parseInt(riskLevel)) && !AUDIT_STATUS_CLOSED.equals(calcDaStatus()))
			return STATUS_INACTIVE;
		if (oBean.isAuditRequired(AuditType.OFFICE,Integer.parseInt(riskLevel)) && !AUDIT_STATUS_CLOSED.equals(officeStatus))
			return STATUS_INACTIVE;
		return STATUS_ACTIVE;
	}

	public String calcPICSStatus(PermissionsBean pBean) throws Exception {
		 //MULTIAUDIT change function calls to get individual audit status
		if (pBean.isOperator())
			return calcPICSStatusForOperator(pBean.oBean);
		setFacilitiesFromDB();
		if (0==facilitiesCount)
			return STATUS_INACTIVE;
		ListIterator<String> li = generalContractors.listIterator();
		OperatorBean tempOBean = new OperatorBean();
		while (li.hasNext()) {
			String opID = (String)li.next();
			tempOBean.setFromDB(opID);
			if (STATUS_INACTIVE.equals(calcPICSStatusForOperator(tempOBean)))
				return STATUS_INACTIVE; 
		}//while
		return STATUS_ACTIVE;
	}//calcPICSStatus

	public boolean isDesktopRequired() throws Exception {
		 //MULTIAUDIT remove method
		setFacilitiesFromDB();
		if (0==facilitiesCount)
			return false;
		ListIterator<String> li = generalContractors.listIterator();
		OperatorBean tempOBean = new OperatorBean();
		while (li.hasNext()) {
			String opID = (String)li.next();
			tempOBean.setFromDB(opID);
			if (tempOBean.canSeeDesktop())
				return true; 
		}//while
		return false;
	}//isDesktopRequired

	/**
	 * If any of this contractor's facilities canSeeDA and 
	 * the contractor daRequired isn't set to No then return true
	 * @return
	 * @throws Exception
	 */
	public boolean isDARequired() throws Exception {
		 //MULTIAUDIT remove method
		if ("No".equals(this.daRequired))
			return false;
		
		setFacilitiesFromDB();
		if (0==facilitiesCount)
			return false;
		ListIterator<String> li = generalContractors.listIterator();
		OperatorBean tempOBean = new OperatorBean();
		while (li.hasNext()) {
			String opID = (String)li.next();
			tempOBean.setFromDB(opID);
			if (tempOBean.canSeeDA())
				return true; 
		}//while
		return false;
	}//isDARequired
	
	public boolean isOfficeRequired() throws Exception {
		 //MULTIAUDIT remove method
		setFacilitiesFromDB();
		if (0==facilitiesCount)
			return false;
		ListIterator<String> li = generalContractors.listIterator();
		OperatorBean tempOBean = new OperatorBean();
		while (li.hasNext()) {
			String opID = (String)li.next();
			tempOBean.setFromDB(opID);
			if (tempOBean.canSeeOffice())
				return true; 
		}//while
		return false;
	}//isOfficeRequired
	
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
	}//isOfficeRequired

	public String getPICSStatus() throws Exception {
		return calcPICSStatus();
	}//getPICSStatus

	public String getAuditStatus() throws Exception {
	//MULTIAUDIT remove method
		return calcAuditStatus();
	}//getAuditStatus

	public String getDesktopStatus() throws Exception {
	//MULTIAUDIT remove method
		return calcDesktopStatus();
	}//getDesktopStatus
	public String getDaStatus() throws Exception {
	//MULTIAUDIT remove method
		return calcDaStatus();
	}//getDaStatus

	public String getOfficeStatusNew() throws Exception {
	//MULTIAUDIT remove method
		return calcOfficeStatusNew();
	}//getOfficeStatusNew

	// audit status is now calculated based on db info, not a seperate column in db
	public String calcAuditStatus() throws Exception {
	//MULTIAUDIT remove method
//		return auditDate;
		boolean tempIsExempt = false;
		if ("Yes".equals(isOnlyCerts))
			return AUDIT_STATUS_EXEMPT;
		if (isExempt())
			tempIsExempt = true;
		if ("".equals(pqfSubmittedDate))
			return AUDIT_STATUS_PQF_PENDING;
		if (DateBean.isPrequalExpired(pqfSubmittedDate))
			return AUDIT_STATUS_PQF_INCOMPLETE;
		if (tempIsExempt)
			return AUDIT_STATUS_EXEMPT;
		if (DateBean.isAfterToday(auditValidUntilDate))
			return AUDIT_STATUS_CLOSED;
		if (DateBean.isAuditExpired(auditCompletedDate) && !DateBean.isAfterToday(auditDate))
			return AUDIT_STATUS_SCHEDULING;
		if (DateBean.isAfterToday(auditDate))
			return AUDIT_STATUS_SCHEDULED;
		if (isAuditCompleted() && !isAuditClosed())
			return AUDIT_STATUS_RQS;
		if (isAuditClosed())
			return AUDIT_STATUS_CLOSED;
		return AUDIT_STATUS_SCHEDULED;	
	}//calcAuditStatus

	public String calcPQFStatus() throws Exception {
	//MULTIAUDIT remove method
		if (!isPQFSubmitted())
			return AUDIT_STATUS_PQF_PENDING;
		if (DateBean.isPrequalExpired(pqfSubmittedDate))
			return AUDIT_STATUS_EXPIRED;
		return AUDIT_STATUS_CLOSED;
	}//calcPQFStatus

	public String calcDesktopStatus() throws Exception {
	//MULTIAUDIT remove method
		if (isExempt())
			return AUDIT_STATUS_EXEMPT;
		if (DateBean.isAfterToday(desktopValidUntilDate))
			return AUDIT_STATUS_CLOSED;
		if (!isDesktopSubmitted())
			return AUDIT_STATUS_VERIFICATION_PENDING;
		if (DateBean.isAuditExpired(desktopSubmittedDate))
			return AUDIT_STATUS_EXPIRED;
		if (!isDesktopClosed())
			return AUDIT_STATUS_RQS;
		return AUDIT_STATUS_CLOSED;
	}//calcDesktopStatus

	public String calcDaStatus() throws Exception {
	//MULTIAUDIT remove method
		if ("No".equals(this.daRequired))
			// The audit isn't required so assume it's closed
			return AUDIT_STATUS_CLOSED;
		if (isExempt())
			return AUDIT_STATUS_EXEMPT;
		if (!isDaSubmitted())
			return AUDIT_STATUS_VERIFICATION_PENDING;
		if (DateBean.isAuditExpired(daSubmittedDate))
			return AUDIT_STATUS_EXPIRED;
		if (!isDaClosed())
			return AUDIT_STATUS_RQS;
		return AUDIT_STATUS_CLOSED;
	}//calcDaStatus

	public boolean isDesktopStatusOldAuditStatus() throws Exception{
	//MULTIAUDIT remove method
		if (!"".equals(auditValidUntilDate) && DateBean.isFirstBeforeSecond(auditValidUntilDate, DateBean.getThreeYearsAheadDate(DateBean.toShowFormat(DateBean.OLD_OFFICE_CUTOFF))))
			return true;
		if (isAuditCompleted() &&
				!"".equals(desktopClosedDate) &&
				DateBean.isFirstBeforeSecond(auditCompletedDate,DateBean.toShowFormat(DateBean.OLD_OFFICE_CUTOFF)) && 
				(AUDIT_STATUS_RQS.equals(calcOfficeStatus()) || AUDIT_STATUS_CLOSED.equals(calcOfficeStatus())))
			return true;
		return false;
	}//isDesktopStatusOldAuditStatus

	public String calcOfficeStatus() throws Exception {
	//MULTIAUDIT rewrite
		if (isExempt())
			return AUDIT_STATUS_EXEMPT;
		if (DateBean.isAfterToday(auditValidUntilDate))
			return AUDIT_STATUS_CLOSED;
		if (!isAuditCompleted()){
			if (DateBean.isAfterToday(auditDate))
				return AUDIT_STATUS_SCHEDULED;
			else
				return AUDIT_STATUS_SCHEDULING;
		}//if
		if (!isAuditClosed() && !DateBean.isAuditExpired(auditCompletedDate))
			return AUDIT_STATUS_RQS;
		if (isAuditClosed() && !DateBean.isAuditExpired(auditCompletedDate))
			return AUDIT_STATUS_CLOSED;
		if (DateBean.isAfterToday(auditDate))
			return AUDIT_STATUS_SCHEDULED;
		if (DateBean.isAuditExpired(auditCompletedDate))
			return AUDIT_STATUS_EXPIRED;
		return "";
	}//calcOfficeStatus

	public String calcOfficeStatusNew() throws Exception {
	//MULTIAUDIT rewrite
		if (isExempt())
			return AUDIT_STATUS_EXEMPT;
		if (!isOfficeSubmitted()){
			if (DateBean.isAfterToday(auditDate))
				return AUDIT_STATUS_SCHEDULED;
			else
				return AUDIT_STATUS_SCHEDULING;
		}//if

		if (!isOfficeClosed() && !DateBean.isAuditExpired(officeSubmittedDate))
			return AUDIT_STATUS_RQS;
		if (isOfficeClosed() && !DateBean.isAuditExpired(officeSubmittedDate))
			return AUDIT_STATUS_CLOSED;
		if (DateBean.isAfterToday(auditDate))
			return AUDIT_STATUS_SCHEDULED;
		if (DateBean.isAuditExpired(officeSubmittedDate))
			return AUDIT_STATUS_EXPIRED;
		return "";
	}//calcOfficeStatusNew

	//For audit status display purposes on contractor details BJ 1-20-05
	public String getAuditStatusShow() throws Exception {
		//MULTIAUDIT rewrite
		return calcAuditStatus();
	}//getAuditStatusShow
	public String getTradesList() {
		return trades.substring(trades.indexOf(";")+1,trades.length()-1).replaceAll(";",", ");
	}//getTradesList
	public String getSubTradesList() {
		if (subTrades.substring(0,1).equals("0"))
			return "";
		return subTrades.substring(subTrades.indexOf(";")+1,subTrades.length()-1).replaceAll(";",", ");
	}
	
	public String getTextColor() throws Exception {
		return getTextColor(calcPICSStatus());
	}//getTextColor

	public static String getTextColor(String s) {
		if (STATUS_ACTIVE.equals(s))
			return "active";
		return "inactive";
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

	public String getPQFLink(PermissionsBean pBean) throws Exception{
		//MULTIAUDIT rewrite
		if ((pBean.isOperator() || pBean.isCorporate()) && !pBean.oBean.canSeePQF())
			return "Contact PICS";
		String status = calcPQFStatus();
		String thisClass = "inactive";
		if (AUDIT_STATUS_CLOSED.equals(status))
			thisClass = "active";
		if (this.canView(pBean.getPermissions(), "pqf"))
			return "<a class="+thisClass+" href=pqf_view.jsp?id="+id+">"+pqfSubmittedDate+"</a>";
		return pqfSubmittedDate;
	}//getPQFLink

	public String getDaLink(PermissionsBean pBean) throws Exception {
		//MULTIAUDIT rewrite
		if ("No".equals(this.daRequired))
			return "Not Required";
		if ((pBean.isOperator() || pBean.isCorporate()) && !pBean.oBean.canSeeDA())
			return "Contact PICS";
		if (isExempt())
			return "Exempt";
		String status = calcDaStatus();
		String thisClass = "inactive";
		if (AUDIT_STATUS_CLOSED.equals(status))
			thisClass = "active";
		if (AUDIT_STATUS_VERIFICATION_PENDING.equals(status))
			return "No";
		else if (!this.canView(pBean.getPermissions(), "daaudit"))
			return "Yes";
		else
			return "<a href=pqf_view.jsp?auditType=DA&id="+id+" class="+thisClass+">"+daSubmittedDate+" ("+status+")"+"</a>";
	}//getDaLink

	public String getDetailsStatus(PermissionsBean pBean) throws Exception {
		String tempStatus = calcPICSStatus(pBean);
		if (pBean.userID.equals(id))	return tempStatus;
		if (!this.canView(pBean.getPermissions(), "daaudit")) return "";
		else return tempStatus;
	}

	public String getBrochureLink() {
		if (!"No".equals(brochure_file))
			return "<a target=_blank href=servlet/showpdf?id="+id+"&file=brochure>Company Brochure</a>";
		return "";
	}//getBrochureLink

	public String getNotesIcon() {
		String temp = notes;
		if (temp.length()>1005)
			temp = temp.substring(0,1000);
		if ("".equals(notes))
			return "<a href=add_notes.jsp?id="+id+"><img src=images/iconGhost_notes.gif alt=Notes width=20 height=20 border=0></a>";
		else
			return "<a href=add_notes.jsp?id="+id+"><img src=images/icon_notes.gif alt=\""+temp+"\" width=20 height=20 border=0></a>";
	}//getNotesIcon

	public void setFromDB(String conID) throws Exception {
		id = conID;
		setFromDB();
	}//setFromDB

	public void setFromDB() throws Exception {
		try{
			if ((null == id) || ("".equals(id)))
				throw new Exception("can't set contractor info from DB because id is not set");
			DBReady();
			String Query = "SELECT * FROM contractor_info WHERE id="+id+";";
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
			setFacilitiesFromDB();
		}finally{
			DBClose();
		}//finally
	}//setFromDB

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
			}//generals
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
	}//setFacilitiesFromDB

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		id = SQLResult.getString("id");
		taxID = SQLResult.getString("taxID");
//		setTaxIDParts(taxID);
		main_trade = SQLResult.getString("main_trade");
		trades = SQLResult.getString("trades");
		subTrades = SQLResult.getString("subTrades");
		logo_file = SQLResult.getString("logo_file");
		welcomeAuditor_id = SQLResult.getString("welcomeAuditor_id");
		brochure_file = SQLResult.getString("brochure_file");
		//fix ms word apostrophes changed to ?
		description = SQLResult.getString("description");
		//fix ms word cut & paste bug, change ? back to apostrophes 
		//checks to see if space after ? so real question marks aren't changed
		//bj 2-21-05
	//	if (description.indexOf('?') > -1)  {
	//		if (!(description.charAt(description.indexOf('?')+1) ==  ' '))					
				description = description.replace('?','\'');
		//}
		certs = SQLResult.getString("certs");
//*************
//db - 46 -4(2)

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
	}//setFromResultSet

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE contractor_info SET "+
			"taxID='"+eqDB(taxID)+
			"',main_trade='"+main_trade+
			"',trades='"+ trades+
			"',subTrades='"+subTrades+ 
			"',logo_file='"+logo_file+
			"',brochure_file='"+brochure_file+
			"',status='"+calcPICSStatus()+
			"',certs='"+certs+
			"',mustPay='"+mustPay+
			"',welcomeAuditor_id='"+welcomeAuditor_id+
			"',requestedByID='"+requestedByID+
			"',billingAmount='"+billingAmount+
			"',billingCycle='"+billingCycle+
			// We now save this in writeBillingToDB()
			//"',isExempt='"+isExempt+
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
//second contact
			"',secondContact='"+eqDB(secondContact)+
			"',secondPhone='"+eqDB(secondPhone)+
			"',secondEmail='"+eqDB(secondEmail)+
//billing contact
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
		}//finally
	}//writeToDB

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
			
			int nextYear = Calendar.getInstance().get(Calendar.YEAR) + 1;
			// Create the starting PQF audit for this contractor (and any others we missed)
			Query = "INSERT INTO contractor_audit (conID, auditTypeID, createdDate, expiresDate) " +
				"SELECT id, 1, dateCreated, '"+nextYear+"-01-01' FROM accounts a WHERE type = 'Contractor' " +
				"AND id not in (SELECT conID FROM contractor_audit WHERE auditTypeID = 1)";
			SQLStatement.executeUpdate(Query);
			DBClose();
			writeToDB();
			
			com.picsauditing.PICS.OperatorBean.resetSubCountTable();
			new com.picsauditing.PICS.pqf.CategoryBean().generateDynamicCategories(id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE, riskLevel);
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
	
//	 New accounts default to inactive BJ 1-7-05
	public void setFromUploadRequestClientNew(javax.servlet.http.HttpServletRequest r) throws Exception {
		taxID = r.getParameter("taxID");
		auditDate = "";
		description = r.getParameter("description");
		isDescriptionChanged = true;
		main_trade = r.getParameter("main_trade");
		setGeneralContractorsFromStringArray(r.getParameterValues("generalContractors"));
		requestedByID = r.getParameter("requestedByID");
//second contact
		secondContact = r.getParameter("secondContact");
		secondEmail = r.getParameter("secondEmail");
		secondPhone = r.getParameter("secondPhone");
//billing contact
		billingContact = r.getParameter("billingContact");
		billingEmail = r.getParameter("billingEmail");
		billingPhone = r.getParameter("billingPhone");

		oqEmployees = r.getParameter("oqEmployees");
		riskLevel = r.getParameter("riskLevel");
	}//setFromUploadRequestClientNew

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
		/*
		if (!primaryUser.isSet)
			primaryUser.setFromAccountID(this.id);
		
		primaryUser.userDO.password = m.get("password");
		*/
		
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

	public void writeAuditEmailDateToDB(String conID, String adminName) throws Exception {
		setFromDB(conID);
		lastAuditEmailDate = DateBean.getTodaysDate(); //MULTIAUDIT change
		addNote(conID, "("+adminName+")", "Audit email sent", DateBean.getTodaysDateTime());
		writeToDB();
	}

	public static String getStatusSelect(String name, String classType, String selectedStatus) throws Exception {
		return Utilities.inputSelect(name, classType, selectedStatus, STATUS_ARRAY);
	}

	public String getAuditorsEmail() throws Exception {
		//MULTIAUDIT change
		String selectQuery = "SELECT email FROM users WHERE id = "+auditor_id;
		String email = "";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				email = SQLResult.getString("email");
			SQLResult.close();
			DBClose();
			return email;
		}finally{
			DBClose();
		}
	}
	
	public void addNote(String conID, String pre, String newNote, String notesDate) throws Exception {
		notes = notesDate+" "+pre+": "+newNote+"\n"+notes;
		isNotesChanged = true;
	}

	public void addAdminNote(String conID, String pre, String newNote, String notesDate) throws Exception {
		adminNotes = notesDate+" "+pre+": "+newNote+"\n"+adminNotes;
		isAdminNotesChanged = true;
	}
	
	public void submitPQF(String conID, Permissions permissions, String auditType) throws Exception {
	//MULTIAUDIT change
		setFromDB(conID);
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.PQF_TYPE)) {
			canEditPrequal = "No";
			pqfSubmittedDate = DateBean.getTodaysDate();
		} else if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE))
			desktopSubmittedDate = DateBean.getTodaysDate();
		else if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DA_TYPE))
			daSubmittedDate = DateBean.getTodaysDate();
		else if (auditType.equals(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)){
			officeSubmittedDate = DateBean.getTodaysDate();
			isNewOfficeAudit = "Yes";
		}//elseif
		addNote(conID, "("+permissions.getName()+")",auditType+" submitted", DateBean.getTodaysDateTime());
		writeToDB();
		
		// Send email too
		EmailContractorBean emailer = new EmailContractorBean();
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE))
			emailer.sendMessage(EmailTemplates.desktopsubmit, conID, permissions);
		else if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DA_TYPE))
			emailer.sendMessage(EmailTemplates.dasubmit, conID, permissions);
		else if (auditType.equals(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE))
			//emailer.sendMessage(EmailTemplates.desktopsubmit, conID, permissions);
			EmailBean.sendAuditSurveyEmail(conID);
	}//submitPQF

	/**
	 * This closes all the other audits
	 * @param conID
	 * @param adminName
	 * @param auditType
	 * @throws Exception
	 */
	public void closeAudit(String conID, String adminName, String auditType) throws Exception {
	//MULTIAUDIT change
		setFromDB(conID);
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE))
			officeClosedDate = DateBean.getTodaysDate();
		else if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE)){
			desktopClosedDate = DateBean.getTodaysDate();
			desktopValidUntilDate = DateBean.getThreeYearsAheadDate(desktopSubmittedDate);
		}
		else if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DA_TYPE))
			daClosedDate = DateBean.getTodaysDate();
		addNote(id,"("+adminName+")",auditType+" Audit closed",DateBean.getTodaysDateTime());
		writeToDB();
		EmailBean.sendAuditClosedEmails(conID,adminName,auditType);
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE))
			EmailBean.sendDesktopClosedSurveyEmail(conID);
	}//closeAudit

	/**
	 * This closes the orignal office audit
	 * @param conID
	 * @param adminName
	 * @throws Exception
	 * @Deprecated
	 */
	public void closeAudit(String conID, String adminName) throws Exception {
	//MULTIAUDIT change
		setFromDB(conID);
		auditClosedDate=DateBean.getTodaysDate();
		auditValidUntilDate = DateBean.getThreeYearsAheadDate(auditCompletedDate);
		addNote(conID, "("+adminName+")", "Office audit closed", DateBean.getTodaysDateTime());
		writeToDB();
		EmailBean.sendAuditClosedEmails(conID, adminName, com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE);
	}//closeAudit

	public void submitAudit(String conID, String adminName) throws Exception {
		//MULTIAUDIT change
		setFromDB(conID);
		auditCompletedDate = DateBean.getTodaysDate();
		addNote(id, "("+adminName+")", "Office audit completed", DateBean.getTodaysDateTime());
		writeToDB();
		EmailBean.sendAuditSurveyEmail(conID);
	}//submitAudit

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
	}//upgradePayment

/*	jj 1-10-07, revamped new payment expires calculation
	public void updateLastPayment(String id, String adminName, String amount) throws Exception {
		setFromDB(id);
		Calendar monthAheadCal = Calendar.getInstance();
		monthAheadCal.add(Calendar.DATE, 35);
//		java.util.Date lastPaymentDate = DateBean.showFormat.parse(lastPayment);
		Calendar newPaymentExpiresCal = Calendar.getInstance();
		if (!"".equals(auditDate))
			newPaymentExpiresCal.setTime(DateBean.showFormat.parse(auditDate));
		while (newPaymentExpiresCal.before(monthAheadCal))
			newPaymentExpiresCal.add(Calendar.YEAR, 1);
		int additionalYears = Integer.parseInt(billingCycle) - 1;
		newPaymentExpiresCal.add(Calendar.YEAR, additionalYears);
		paymentExpires = DateBean.showFormat.format(newPaymentExpiresCal.getTime());
		lastPayment = DateBean.getTodaysDate();
		billingAmount = amount;
//		addNote(id, "("+adminName+")", "Payment recorded for "+billingCycle+" years, expires on "+paymentExpires, DateBean.getTodaysDateTime());
		writeToDB();

//		monthAheadCal.add(Calendar.DAY, 35);
//		java.util.Date today = cal.getTime();
//		java.util.Date monthAhead = cal.getTime();
//		java.util.Date newPaymentExpires = newPaymentExpiresCal.getTime();
//		java.util.Date today = DateBean.showFormat.parse(lastPayment);
//		String Query = "UPDATE contractor_info SET lastPayment = NOW() WHERE id = '" + id + "';";
//		SQLStatement.executeUpdate(Query);
//		DBClose();
	}//updateLastPayment
*/
	//checks to see if auditor already has audit scheduled for this date. Called from report_scheduleAudits
	//bj 4-5-05
	public String checkDoubleAudit(String action_id) throws Exception {
	//MULTIAUDIT change
		try {
			String returnStr = "";
			if (!"".equals(auditDate)) {
				String Query = "SELECT * FROM blockedDates WHERE blockedDate='"+DateBean.toDBFormat(auditDate) +"';";
				DBReady();
				ResultSet SQLResult = SQLStatement.executeQuery(Query);
				if 	(SQLResult.next()) {
					returnStr =   "<b>"+SQLResult.getString("description")+"</b> is scheduled on "+auditDate;
					if (!"0".equals(SQLResult.getString("startHour"))) 
						returnStr +=" from "+ SQLResult.getString("startHour")+SQLResult.getString("startAmPm")+
						" to "+ SQLResult.getString("endHour")+ SQLResult.getString("endAmPm")+".";
				} else if (!"".equals(auditor_id) && !"0".equals(auditor_id)) {
					SQLResult.close();
					Query = "SELECT contractor_info.id AS con_id, auditHour, auditAmPm, accounts.name AS name, "+
					"a2.name AS auditor_name FROM contractor_info INNER JOIN accounts ON contractor_info.id=accounts.id "+
					"LEFT OUTER JOIN users a2 ON contractor_info.auditor_id=a2.id WHERE auditDate='"+
					DateBean.toDBFormat(auditDate)+"' AND auditor_id=" + auditor_id;
					SQLResult = SQLStatement.executeQuery(Query);
					if 	(SQLResult.next()) 
						if (!action_id.equals(SQLResult.getString("con_id")))
							returnStr = "<b>"+SQLResult.getString("auditor_name")+"</b> has an audit scheduled on <b>"+
							auditDate+"</b> at <b>"+SQLResult.getString("auditHour")+SQLResult.getString("auditAmPm")+
							"</b> with <b>"+SQLResult.getString("name")+"</b>.";
						else
							returnStr = "";
					else
						returnStr = "";
					SQLResult.close();
				}
				DBClose();
			} else
				returnStr = "";
			return returnStr;
		}finally{
			DBClose();
		}//finally
	}

	public boolean isExempt() {
	//MULTIAUDIT remove
		return isAudited();
	}
	
	public boolean isAudited() {
	//MULTIAUDIT remove
		return "Yes".equals(this.isExempt);
	}
	public void isAudited(boolean value) {
	//MULTIAUDIT remove
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

	public boolean mustForceUpdatePQF() throws Exception {
	//MULTIAUDIT remove
		if ("".equals(pqfSubmittedDate))
			return false;
		try{
			return DateBean.isFirstBeforeSecond(pqfSubmittedDate,"12/31/05");
		}catch(Exception ex){
			return false;
		}//catch
	}//mustForceUpatePFQ

	public String getAuditSubmittedDate(String auditType) {
		//MULTIAUDIT change
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.PQF_TYPE))
			return pqfSubmittedDate;
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE))
			return desktopSubmittedDate;
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DA_TYPE))
			return daSubmittedDate;
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE))
			return officeSubmittedDate;
		return "No";
	}//getAuditSubmittedDate

	public String getAuditClosedDate(String auditType) {
		//MULTIAUDIT change
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE))
			return desktopClosedDate;
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DA_TYPE))
			return daClosedDate;
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE))
			return officeClosedDate;
		return "No";
	}//getAuditClosedDate

	public String getValidUntilDate(String auditType) throws Exception{
		//MULTIAUDIT change
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE) && !DateBean.isBeforeToday(desktopValidUntilDate))
			return "<br/>Valid Until: <span class='redMain'>"+desktopValidUntilDate+"</span>";
		if (auditType.equals(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE) && !DateBean.isBeforeToday(auditValidUntilDate))
			return "<br/>Valid Until: <span class='redMain'>"+auditValidUntilDate+"</span>";
		return "";
	}//getValidUntilDate

	public void setPercentComplete(String auditType, Integer percent) {
		setPercentComplete(auditType, percent.toString());
	}
	public void setPercentComplete(String auditType, String percent) {
		//MULTIAUDIT change
		if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType))
			pqfPercent = percent;
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType))
			desktopPercent = percent;
		if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType))
			officePercent = percent;
		if (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType))
			daPercent = percent;
	}//setPercentComplete

	public String getPercentComplete(String auditType) {
		//MULTIAUDIT change
		if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType))
			return pqfPercent;
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType))
			return desktopPercent;
		if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType))
			return officePercent;
		if (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType))
			return daPercent;
		return "Invalid Audit Type";
	}//getPercentComplete

	public void setPercentVerified(String auditType, String percent) {
		//MULTIAUDIT change
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType))
			desktopVerifiedPercent = percent;
		if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType))
			officeVerifiedPercent = percent;
		if (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType))
			daVerifiedPercent = percent;
	}//setPercentVerified

	public String getPercentVerified(String auditType) {
		//MULTIAUDIT change
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType))
			return desktopVerifiedPercent;
		if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType))
			return officeVerifiedPercent;
		if (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType))
			return daVerifiedPercent;
		return "Invalid Audit Type";
	}//getPercentVerified

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
}