package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

import com.picsauditing.PICS.*;
import com.picsauditing.domain.IPicsDO;



public class SearchBean {
/*	History
	2/20/06 jj - eliminated other searches (operator, auditor, osha)
	1/28/05 jj - added doOSHASearch for fatalities and incidence report
	1/19/04 bj - changed scheduleAuditsReport variable to whichScheduleAuditsReport so schedule audit report can return either new or recsheduling audits
	12/31/04 jj - reset incompleteAfter to "" in closeSearch, fixed bug of remembering incomplete audit search because SearchBean is session type
*/
	public AccountBean aBean = new AccountBean();
	public ContractorBean cBean = new ContractorBean();
	public OperatorBean oBean = new OperatorBean();
	public OSHABean osBean = new OSHABean();

	public String emr1 = "";
	public String emr2 = "";
	public String emr3 = "";
	public String emrAve = "";
	public String q318 = "";
	public String q1385 = "";
	public String manualRevisionDate = "";
	public String tradePerformedBy = "";

	HashSet hasCertSet = null;
	HashSet canSeeSet = null;
	HashSet auditorCanSeeSet = null;

	String changed = "";
	String thisPage = "";
	public String selected_name = "";
	public String selected_industry = "";
	public String selected_trade = "";
	public String selected_startsWith = "";
	public String selected_zip = "";
	public String selected_city = "";
	public String selected_state = "";
	public String selected_status = "";
	public String selected_auditStatus = "";
	public String selected_auditType = "";
	public String selected_generalContractorID = ""; // only used when admin wants to see sub contractors of certain general contractor
	public String selected_certsOnly = "";
	public String selected_entireDB = "";
	public String selected_pqfAuditorID = "";
	public String selected_desktopAuditorID = "";
	public String selected_daAuditorID = "";
	public String selected_officeAuditorID = "";
	public String selected_incompleteAfter = "";
	public String selected_invoicedStatus = "";
    public String searchCorporate = "";
	public String Query = "";
	public String selected_auditLocation = "";
	public String selected_visible = "";
	public String selected_stateLicensedIn = "";
	public String selected_flagStatus = "";
	public String selected_taxID = "";
	public String selected_performedBy = "";

	public String expiresInDays = "";
	public String searchType = DEFAULT_TYPE;
//	public String prequalExpiresInDays = "";
//	public boolean isAnnualUpdateReport = false;
//	public boolean isAuditorsReport = false;
	public boolean isPaymentReport = false;
	public boolean isUpgradePaymentReport = false;
	public boolean isDesktopReport = false;
	public boolean isActivationReport = false;
	public boolean isNoInsuranceOnly = false;
	public boolean isOSHASearch = false;
	public boolean isNCMSReport = false;
	public boolean isHurdleRatesReport = false;
	public boolean isEMRRatesReport = false;
	public int auditCalendarMonth = 0;
	public int auditCalendarYear = 0;
	
	public String orderBy = "name";	// the result set is sorted on this colunm, can be changed
	public void setOrderByColumn(String s) {orderBy = s;}
	String whichScheduleAuditsReport = "";
		
	Connection Conn = null;
	Statement SQLStatement = null;
	public ResultSet SQLResult = null;
	
	public int numResults = 0;	
	
	public int count = 0;
	int beginResults = 0;
	int endResults = 0;
	public int showPage = 0;
	int showNum = 0;

	public static final int MIN_NAME_SEARCH_LENGTH = 3;
	public static final String DEFAULT_TYPE = "Contractor";
	public static final String DEFAULT_NAME = "- Name - ";
	public static final String DEFAULT_INDUSTRY = "- Industry -";
	public static final String DEFAULT_TRADE = TradesBean.DEFAULT_SELECT_TRADE_ID;
	public static final String DEFAULT_PERFORMED_BY = TradesBean.DEFAULT_PERFORMED_BY;
	public static final String DEFAULT_ZIP = "- Zip -";
	public static final String DEFAULT_CITY = "- City -";
	public static final String DEFAULT_STATE = "- State -";
	public static final String DEFAULT_AUDIT_STATUS = "- Audit Status -";
	public static final String DEFAULT_AUDITOR = "- Auditor -";
	public static final String DEFAULT_AUDITOR_ID = "0";
	public static final String DEFAULT_STATUS = "- Status -";
	public static final String DEFAULT_GENERAL = "- Operator -";
	public static final String DEFAULT_AUDIT_LOCATION = "- Audit Location -";
	public static final String DEFAULT_VISIBLE = "- Visible -";
	public static final String DEFAULT_GENERAL_VALUE = "-2";
	public static final String DEFAULT_CERTS = "- Ins. Certs -";
	public static final String DEFAULT_INVOICED_STATUS = "- Invoiced Status -";
	public static final String DEFAULT_LICENSED_IN = "- Licensed In -";
	public static final String DEFAULT_LICENSED_IN_ID = com.picsauditing.PICS.pqf.QuestionTypeList.DEFAULT_SELECT_QUESTION_ID;
	public static final String DEFAULT_FLAG_STATUS = "- Flag Status -";
	public static final String DEFAULT_TAX_ID = "- Tax ID -";
	public static final String ONLY_CERTS = "Only Certs";
	public static final String EXCLUDE_CERTS = "Exclude Certs";
	public static final String[] CERTS_SEARCH_ARRAY = {DEFAULT_CERTS,ONLY_CERTS,EXCLUDE_CERTS};
	public static final String[] AUDITLOCATION_SEARCH_ARRAY = {DEFAULT_AUDIT_LOCATION,"On Site","Web"};
	public static final String[] INVOICED_SEARCH_ARRAY = {DEFAULT_INVOICED_STATUS,"Invoiced","Not Invoiced"};
	public static final String[] VISIBLE_SEARCH_ARRAY = {DEFAULT_VISIBLE,"Y","N"};
	public static final boolean LIST_DEFAULT = true;
	public static final boolean DONT_LIST_DEFAULT = false;
	public static final boolean ONLY_ACTIVE = true;
	public static final boolean ACTIVE_AND_NOT = false;
	public static final boolean SHOW_ALL = true;
	public static final boolean DONT_SHOW_ALL = false;
	public boolean showAll = false;
	public static final String ADMIN_ID = AccountBean.ADMIN_ID;
	static final String[] INDUSTRY_SEARCH_ARRAY = {DEFAULT_INDUSTRY,"Petrochemical","Mining","Power","General",
											"Construction","Manufacturing"};		
	static final String[] ZIP_SEARCH_ARRAY = {DEFAULT_ZIP,"9","8","7","6","5","4","3","2","1","0"};		
	public static final String[] FLAG_STATUS_ARRAY = {DEFAULT_FLAG_STATUS,"Green","Amber","Red"};
	public static final String NEW_AUDITS = "new";
	public static final String RESCHEDULE_AUDITS = "reschedule";

	public static TradesBean tBean = null;
	public String getSearchTrade() throws Exception {
		if (null == tBean)
			tBean = new TradesBean();
		if (DEFAULT_TRADE.equals(selected_trade))	return Utilities.escapeHTML(cBean.main_trade);
		else	return Utilities.escapeHTML(tBean.getTradeFromID(selected_trade));
	}//getSearchTrade

	public int getNumResults() {return numResults;}//getNumResults
	
	public String getBGColor() {
		if ((count % 2) == 1)	return " bgcolor=\"#FFFFFF\"";
		else	return "";
	}//getBGColor

	public String getCellBGColor() {
		if (!canSeeContractor())	return "bgcolor=\"#DDDDDD\"";
		else return "";
	}//getBGColor

	public String getAuditDateLink() throws Exception {
		String temp = cBean.auditStatus;
		if (!canSeeContractor())
			return "";
		else if (ContractorBean.AUDIT_STATUS_EXEMPT.equals(temp) || ContractorBean.AUDIT_STATUS_PQF_PENDING.equals(temp) 
				|| ContractorBean.AUDIT_STATUS_PQF_INCOMPLETE.equals(temp) 
				|| ContractorBean.AUDIT_STATUS_SCHEDULING.equals(temp) || ContractorBean.AUDIT_STATUS_SCHEDULED.equals(temp))
			return temp;
		else if (ContractorBean.AUDIT_STATUS_RQS.equals(temp) || ContractorBean.AUDIT_STATUS_CLOSED.equals(temp))
			return "<a href=audit_view.jsp?id="+aBean.id+" class=\""+getTextColor()+"\">"+cBean.getAuditDateShow()+"</a>" ;
		else
			return "<a href=\"/servlet/showpdf?id="+aBean.id+"&file=audit\" target=\"_blank\" class=\""+
					getTextColor()+"\">"+cBean.getAuditDateShow()+"</a>";
	}//getAuditDateLink

	public String getCertsLink() {
		if (null != hasCertSet && hasCertSet.contains(aBean.id))
			if (!canSeeContractor())
				return "<img src=\"images/iconGhost_insurance.gif\" width=\"20\" height=\"20\" border=\"0\"></a>";				
			else
				return "<a href=\"certificates_view.jsp?id="+aBean.id+"\">"+
					"<img src=\"images/icon_insurance.gif\" width=\"20\" height=\"20\" border=\"0\"></a>";
		return "";
	}//getCertsLink

	public String getCertsAdminLink() {
		if ("0".equals(cBean.certs))
			return "";
		else
			return "<a href=\"contractor_upload_certificates.jsp?id="+aBean.id+"\">"+
				"<img src=\"images/icon_insurance.gif\" width=\"20\" height=\"20\" border=\"0\"></a>";
	}//getCertsAdminLink

	public void doSearch(javax.servlet.http.HttpServletRequest r, boolean onlyActive, int resultsOnPage, 
						PermissionsBean permissions, String accessID) throws Exception {
		String accessType = permissions.userType;
		showNum = resultsOnPage;
		changed = r.getParameter("changed");

		String whereQuery = "";
		String groupByQuery = "";
		String pqfJoinQuery = "";
		String oshaJoinQuery = "";
		String joinQuery = "";
		String ncmsJoinQuery = "";

		if ((null == changed) || ("1".equals(changed))) {
// if it's a new search, reset all the search parameters
			showPage = 1;
			selected_name = r.getParameter("name");
			selected_industry = r.getParameter("industry");
			selected_trade = r.getParameter("trade");
			selected_startsWith = r.getParameter("startsWith");
			selected_zip = r.getParameter("zip");
			selected_city = r.getParameter("city");
			selected_state = r.getParameter("state");
			selected_status = r.getParameter("status");
			selected_auditStatus = r.getParameter("auditStatus");
			selected_auditType = r.getParameter("auditType");
			selected_generalContractorID = r.getParameter("generalContractorID");
			selected_certsOnly = r.getParameter("certsOnly");
			selected_entireDB = r.getParameter("entireDB");
			selected_pqfAuditorID = r.getParameter("pqfAuditorID");
			selected_desktopAuditorID = r.getParameter("desktopAuditorID");
			selected_daAuditorID = r.getParameter("daAuditorID");
			selected_officeAuditorID = r.getParameter("officeAuditorID");

            searchCorporate = r.getParameter("searchCorporate");

			selected_searchYear1 = r.getParameter("searchYear1");
			selected_searchYear2 = r.getParameter("searchYear2");
			selected_searchYear3 = r.getParameter("searchYear3");
			searchEMRRate = r.getParameter("searchEMRRate");
			if (null==searchEMRRate)
				searchEMRRate = "";

			searchIncidenceRate = r.getParameter("searchIncidenceRate");
//			screenDirection = r.getParameter("screenDirection");
			if (null==searchIncidenceRate)
				searchIncidenceRate = "";
			selected_incompleteAfter = r.getParameter("incompleteAfter");
			if (null==selected_incompleteAfter)
				selected_incompleteAfter = "";
			selected_invoicedStatus = r.getParameter("invoicedStatus");
			selected_auditLocation = r.getParameter("auditLocation");
			selected_visible = r.getParameter("visible");
			selected_stateLicensedIn = r.getParameter("stateLicensedIn");
			selected_flagStatus = r.getParameter("flagStatus");
			selected_taxID = r.getParameter("taxID");
			selected_performedBy = r.getParameter("performedBy");
		}//if
		else
			showPage = Integer.parseInt(r.getParameter("showPage"));

		if ("Y".equals(selected_entireDB)){
			if (null == searchCorporate)
				joinQuery += "LEFT JOIN flags ON flags.conID=accounts.id ";
			whereQuery += "AND (flags.opID IS NULL OR flags.opID="+accessID+") ";
			accessType = "Admin";
		}//if
//Set all the Queries
		if (onlyActive)
			whereQuery += "AND active='Y' ";
		if (null==searchType)
			searchType = DEFAULT_TYPE;
		whereQuery += "AND type='"+searchType+"' ";
		if ((selected_name == null) || (selected_name.equals(DEFAULT_NAME)) || (selected_name.length()<MIN_NAME_SEARCH_LENGTH))
			selected_name = DEFAULT_NAME;
		else
			whereQuery += "AND name LIKE '%"+Utilities.escapeQuotes(selected_name)+"%' ";
		if ((selected_industry == null) || (selected_industry.equals(DEFAULT_INDUSTRY)))
			selected_industry = DEFAULT_INDUSTRY;
		else
			whereQuery += "AND industry='"+selected_industry+"' ";
		if ((selected_trade == null) || (selected_trade.equals(DEFAULT_TRADE)))
			selected_trade = DEFAULT_TRADE;
		else {
//////////////
			whereQuery += "AND (tradeQ.questionID='"+selected_trade+"' ";
			joinQuery += "INNER JOIN pqfData tradeQ ON (tradeQ.conID=accounts.id) ";
			if ((selected_performedBy == null) || (selected_performedBy.equals(DEFAULT_PERFORMED_BY))){
				selected_performedBy = DEFAULT_PERFORMED_BY;
				whereQuery += "AND tradeQ.answer<>'') ";
			}else
				if ("Sub Contracted".equals(selected_performedBy))
					whereQuery += "AND tradeQ.answer IN (' S','C S')) ";
				else if	("Self Performed".equals(selected_performedBy))
					whereQuery += "AND tradeQ.answer IN ('C  ','C S')) ";					
		}//else
//////
		if ((selected_stateLicensedIn == null) || (selected_stateLicensedIn.equals(DEFAULT_LICENSED_IN_ID)))
			selected_stateLicensedIn = DEFAULT_LICENSED_IN_ID;
		else {
			whereQuery += "AND (licensedInQ.questionID='"+selected_stateLicensedIn+"' AND licensedInQ.answer<>'') ";
			joinQuery += "INNER JOIN pqfData licensedInQ ON (licensedInQ.conID=accounts.id) ";
		}//else
		if ((selected_flagStatus == null) || (selected_flagStatus.equals(DEFAULT_FLAG_STATUS)))
			selected_flagStatus = DEFAULT_FLAG_STATUS;
		else
			whereQuery += "AND flags.flag='"+selected_flagStatus+"' ";
		if ((selected_taxID == null) || "".equals(selected_taxID) || (selected_taxID.equals(DEFAULT_TAX_ID)))
			selected_taxID = DEFAULT_TAX_ID;
		else
			whereQuery += "AND taxID='"+selected_taxID+"' ";		
		if (selected_startsWith != null)
			whereQuery += "AND name LIKE '"+Utilities.escapeQuotes(selected_startsWith)+"%' ";
		if ((selected_zip == null) || (selected_zip.equals(DEFAULT_ZIP)) || ("".equals(selected_zip)))
			selected_zip = DEFAULT_ZIP;
		else
			whereQuery += "AND zip LIKE '"+Utilities.escapeQuotes(selected_zip)+"%' ";
		if ((selected_city == null) || (selected_city.equals(DEFAULT_CITY)) || ("".equals(selected_city)))
			selected_city = DEFAULT_CITY;
		else
			whereQuery += "AND city LIKE '%"+Utilities.escapeQuotes(selected_city)+"%' ";
		if ((selected_state == null) || (selected_state.equals(DEFAULT_STATE)) || ("".equals(selected_state)))
			selected_state = DEFAULT_STATE;
		else
			whereQuery += "AND state='"+selected_state+"' ";
		if ((selected_generalContractorID != null) && !"".equals(selected_generalContractorID) && !DEFAULT_GENERAL_VALUE.equals(selected_generalContractorID)) {
			accessID = selected_generalContractorID;
			accessType = "Operator";
		}//if
		if ((selected_status == null) || (selected_status.equals(DEFAULT_STATUS)) || ("".equals(selected_status)))
			selected_status = DEFAULT_STATUS;
		else{
//****************** mimicks logic in ContractorBean.calcPICSStatusForOperator(PermissionBean)
			if ("Active".equals(selected_status))
				whereQuery+="AND (1 ";
			else
				whereQuery+="AND !(1 ";
			if (permissions.oBean.canSeePQF())
				whereQuery += "AND (pqfSubmittedDate<>'0000-00-00' AND pqfSubmittedDate>'"+DateBean.PQF_EXPIRED_CUTOFF+"') ";
			whereQuery += "AND (isExempt='Yes' OR (1 ";
			if (permissions.oBean.canSeeDesktop())
				whereQuery += "AND (auditValidUntilDate>CURDATE() OR desktopValidUntilDate>CURDATE() OR "+
					"(desktopSubmittedDate<>'0000-00-00' AND desktopSubmittedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR) "+
					"AND desktopClosedDate<>'0000-00-00') OR (auditCompletedDate<>'0000-00-00' AND "+
					"auditCompletedDate<'"+DateBean.OLD_OFFICE_CUTOFF+"' AND auditCompletedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR) "+
					"AND auditClosedDate<>'0000-00-00')) ";
			if (permissions.oBean.canSeeDA())
				whereQuery += "AND (daSubmittedDate<>'0000-00-00' AND daSubmittedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR) "+
					"AND daClosedDate<>'0000-00-00') ";
			if (permissions.oBean.canSeeOffice())
				whereQuery += "AND (auditValidUntilDate>CURDATE() OR " +
						"(auditCompletedDate<>'0000-00-00' AND auditCompletedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR) "+
					"AND auditClosedDate<>'0000-00-00')) ";
			whereQuery+="))) ";
//********************		
		}//else
		if ((selected_auditStatus == null) || (selected_auditStatus.equals(DEFAULT_AUDIT_STATUS)) || ("".equals(selected_auditStatus)))
			selected_auditStatus = DEFAULT_AUDIT_STATUS;
		else
			whereQuery += "AND auditStatus='"+selected_auditStatus+"' ";
		if (selected_pqfAuditorID == null || selected_pqfAuditorID.equals(DEFAULT_AUDITOR_ID))
			selected_pqfAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND pqfAuditor_id="+selected_pqfAuditorID+" ";
		if (selected_desktopAuditorID == null || selected_desktopAuditorID.equals(DEFAULT_AUDITOR_ID))
			selected_desktopAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND desktopAuditor_id="+selected_desktopAuditorID+" ";
		if (selected_daAuditorID == null || selected_daAuditorID.equals(DEFAULT_AUDITOR_ID))
			selected_daAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND daAuditor_id="+selected_daAuditorID+" ";
		if (selected_officeAuditorID == null || selected_officeAuditorID.equals(DEFAULT_AUDITOR_ID))
			selected_officeAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND auditor_id="+selected_officeAuditorID+" ";
		if (ONLY_CERTS.equals(selected_certsOnly))
			whereQuery += "AND isOnlyCerts='Yes' ";
		if (EXCLUDE_CERTS.equals(selected_certsOnly))
			whereQuery += "AND isOnlyCerts<>'Yes' ";
		if (null != searchCorporate)
            accessType = "Corporate";
        if (isActivationReport) {
			//whereQuery += "AND (accountDate='0000-00-00' OR welcomeCallDate='0000-00-00') ";
        	// TJA 11/21/2007 MySQL gets mixed up sometimes with OR clauses.
        	// Also added indexes to accountDate and welcomeCallDate columns
    		whereQuery += "AND accounts.id IN (SELECT id FROM contractor_info WHERE accountDate='0000-00-00' UNION SELECT id FROM contractor_info WHERE welcomeCallDate='0000-00-00') ";
        }
		if (isNoInsuranceOnly)
			whereQuery += "AND isOnlyCerts='No' ";
		//for incomplete audit report
		if (!"".equals(selected_incompleteAfter) && 
				(permissions.oBean.canSeeDesktop() || permissions.oBean.canSeeDA() || permissions.oBean.canSeeOffice())){
			whereQuery +="AND (";
			if (permissions.oBean.canSeeDesktop())
				whereQuery += "((desktopSubmittedDate<>'0000-00-00' AND desktopSubmittedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) "+"AND desktopClosedDate='0000-00-00') OR (desktopSubmittedDate='0000-00-00' AND "+
						"auditCompletedDate<>'0000-00-00' AND "+
						"auditCompletedDate<'"+DateBean.OLD_OFFICE_CUTOFF+"' AND auditCompletedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) AND auditClosedDate='0000-00-00')) ";
			if (permissions.oBean.canSeeDesktop() && permissions.oBean.canSeeDA())
				whereQuery +=" OR ";
			if (permissions.oBean.canSeeDA())
				whereQuery += "(daSubmittedDate<>'0000-00-00' AND daSubmittedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) AND daClosedDate='0000-00-00')";
			if ((permissions.oBean.canSeeDesktop() || permissions.oBean.canSeeDA())
						&& permissions.oBean.canSeeOffice())
				whereQuery +=" OR ";
			if (permissions.oBean.canSeeOffice())
				whereQuery += "(auditCompletedDate<>'0000-00-00' AND auditCompletedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) AND auditClosedDate='0000-00-00')";
			whereQuery +=")";
		}//if
		if ((selected_invoicedStatus == null) || (selected_invoicedStatus.equals(DEFAULT_INVOICED_STATUS)))
			selected_invoicedStatus = DEFAULT_INVOICED_STATUS;
		else if ("Invoiced".equals(selected_invoicedStatus))
			whereQuery += "AND billingAmount=newBillingAmount ";
		else if ("Not Invoiced".equals(selected_invoicedStatus))
			whereQuery += "AND billingAmount<>newBillingAmount ";
		if ((selected_auditLocation == null) || (selected_auditLocation.equals(DEFAULT_AUDIT_LOCATION)))
			selected_auditLocation = DEFAULT_AUDIT_LOCATION;
		else
			whereQuery += "AND auditLocation='"+selected_auditLocation+"' ";
		if ((selected_visible==null) || (selected_visible.equals(DEFAULT_VISIBLE)))
			selected_visible = DEFAULT_VISIBLE;
		else
			whereQuery += "AND active='"+selected_visible+"' ";
		if (!"".equals(expiresInDays))
			whereQuery += "AND (auditCompletedDate <> '0000-00-00' AND DATE_ADD(auditCompletedDate,INTERVAL 3 YEAR) < DATE_ADD(CURDATE(),INTERVAL "+
					expiresInDays+" DAY)) ";
//	jj 9/30/06		whereQuery += "AND ((DATE_ADD(auditDate,INTERVAL 3 YEAR) < DATE_ADD(CURDATE(),INTERVAL "+ expiresInDays +" DAY) AND auditDate <> '0000-00-00') OR (auditDate = '0000-00-00' AND DATE_ADD(lastAuditDate,INTERVAL 3 YEAR) < DATE_ADD(CURDATE(),INTERVAL "+ expiresInDays +" DAY)))  ";

		//	expiresQuery = "AND auditDate < (CURDATE() - INTERVAL 33 MONTH) AND auditDate <> '0000-00-00' ";
//	jj 7-7-06
//		if (isAnnualUpdateReport)
//			whereQuery += "AND (YEAR(pqfSubmittedDate) < YEAR(CURDATE())) ";
		if (isDesktopReport) {
//			whereQuery += "AND pqfSubmittedDate <> '0000-00-00' AND (desktopCompletedDate = '0000-000-00' OR desktopCompletedDate < DATE_ADD(CURDATE(),INTERVAL -34 MONTH)) ";
			whereQuery += "AND isExempt='No' AND (desktopSubmittedDate='0000-000-00' OR desktopSubmittedDate < DATE_ADD(CURDATE(),INTERVAL -34 MONTH)) AND "+
					"!(auditCompletedDate<>'0000-00-00' AND "+
					"auditCompletedDate<'"+DateBean.OLD_OFFICE_CUTOFF+"' AND auditCompletedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR)) ";
			pqfJoinQuery = "INNER JOIN pqfData manQ ON (manQ.conID=accounts.id AND manQ.questionID="+com.picsauditing.PICS.pqf.Constants.MANUAL_PQF_QID+") "+
					"LEFT JOIN pqfData manRevisionQ ON (manRevisionQ.conID=accounts.id AND manRevisionQ.questionID="+com.picsauditing.PICS.pqf.Constants.MANUAL_REVISION_QID+") ";
		}//if
		if (isPaymentReport && !showAll)
			whereQuery += "AND mustPay='Yes' AND (paymentExpires='0000-00-00' OR DATE_ADD(CURDATE(),INTERVAL 35 DAY)>paymentExpires) ";
		if (isPaymentReport && showAll)
			whereQuery += "AND mustPay='Yes'";
		if (isUpgradePaymentReport)
			whereQuery += "AND (lastPaymentAmount<newBillingAmount OR (billingCycle>1 AND newbillingAmount>=799)) "+
					"AND mustPay='Yes' AND isExempt='No' AND (paymentExpires>DATE_ADD(CURDATE(),INTERVAL 90 DAY) "+
					"OR lastInvoiceDate>lastPayment) ";
		if (auditCalendarMonth>0 && auditCalendarYear>0)
			whereQuery += "AND EXTRACT(YEAR_MONTH FROM auditDate)='"+auditCalendarYear+"_"+auditCalendarMonth+"')";
		//for schedule/reschedule audit report
		if (RESCHEDULE_AUDITS.equals(whichScheduleAuditsReport))
//		 	scheduleAuditsQuery = "AND auditStatus IN ('"+cBean.AUDIT_STATUS_RESCHEDULING+"','"+cBean.AUDIT_STATUS_RESCHEDULED+"') OR DATE_ADD(auditDate,INTERVAL 3 YEAR) < DATE_ADD(CURDATE(),INTERVAL 90 DAY) ";
		 	whereQuery += "AND DATE_ADD(auditDate,INTERVAL 3 YEAR) < DATE_ADD(CURDATE(),INTERVAL 90 DAY) OR (auditDate > CURDATE() AND lastAuditDate <> '') ";
		if (NEW_AUDITS.equals(whichScheduleAuditsReport)){
// jj 7-6-06			whereQuery += "AND (auditDate>DATE_ADD(CURDATE(),INTERVAL -1 DAY) OR auditStatus IN ('"+cBean.AUDIT_STATUS_SCHEDULING+"','"+cBean.AUDIT_STATUS_SCHEDULED+"')) ";
			whereQuery += "AND isExempt='No' AND pqfSubmittedDate>'"+DateBean.PQF_EXPIRED_CUTOFF+"' AND (auditCompletedDate='0000-00-00' OR auditCompletedDate<DATE_ADD(CURDATE(),INTERVAL -34 MONTH)) ";
			groupByQuery = "GROUP BY accounts.id ";
			joinQuery+="INNER JOIN generalContractors gcTable ON (gcTable.subID=accounts.id) INNER JOIN operators ON (genID=operators.id AND canSeeOffice='Yes') ";
		}//if
//OSHA Queries
		if (!"".equals(searchIncidenceRate)) {
			whereQuery += "AND (0 ";
			if ((selected_searchYear1 != null))
				whereQuery += "OR ((recordableTotal1*200000)/manHours1 >= "+searchIncidenceRate+") ";
//				incidenceQuery += "OR ((injuryTotal1*200000)/manHours1 "+screenDirection+" "+searchIncidenceRate + ") " ;
			if ((selected_searchYear2 != null))
				whereQuery += "OR ((recordableTotal2*200000)/manHours2 >= "+searchIncidenceRate+") ";
//				incidenceQuery += "OR ((injuryTotal2*200000)/manHours2 "+screenDirection+" "+searchIncidenceRate + ") " ;
			if ((selected_searchYear3 != null))
				whereQuery += "OR ((recordableTotal3*200000)/manHours3 >= "+searchIncidenceRate+") ";
//				incidenceQuery += "OR ((injuryTotal3*200000)/manHours3 "+screenDirection+" "+searchIncidenceRate + ") " ;
			whereQuery += ") ";
		}//if
		if (isFatalitiesReport)
			whereQuery += "AND (fatalities1>0 OR fatalities2>0 OR fatalities3>0) ";
		if (isOSHASearch)
			oshaJoinQuery = "INNER JOIN OSHA ON OSHA.conID=accounts.id ";
		if (isNCMSReport) {
			whereQuery += "AND remove='No' ";
			ncmsJoinQuery = "INNER JOIN NCMS_Desktop ON ((contractor_info.taxID=NCMS_Desktop.fedTaxID AND taxID!='') OR "+
				"accounts.name=NCMS_Desktop.ContractorsName) ";
		}
		if (isHurdleRatesReport){
			pqfJoinQuery = "LEFT JOIN pqfData q1 ON (q1.conID=accounts.id AND q1.questionID="+
				com.picsauditing.PICS.pqf.Constants.EMR_YEAR1+") "+
				"LEFT JOIN pqfData q2 ON (q2.conID=accounts.id AND q2.questionID="+
				com.picsauditing.PICS.pqf.Constants.EMR_YEAR2+") "+
				"LEFT JOIN pqfData q3 ON (q3.conID=accounts.id AND q3.questionID="+
				com.picsauditing.PICS.pqf.Constants.EMR_YEAR3+") "+
				"LEFT JOIN pqfData q1385 ON (q1385.conID=accounts.id AND q1385.questionID=1385) "+
				"LEFT JOIN pqfData q318 ON (q318.conID=accounts.id AND q318.questionID=318) ";
			if(!"".equals(searchEMRRate))
				whereQuery+=" AND (q1.verifiedAnswer>"+searchEMRRate+" OR q2.verifiedAnswer>"+searchEMRRate+" OR q3.verifiedAnswer>"+searchEMRRate+") ";
		}//if
		if (isEMRRatesReport){
			pqfJoinQuery = "LEFT JOIN pqfData q1 ON (q1.conID=accounts.id AND q1.questionID="+
				com.picsauditing.PICS.pqf.Constants.EMR_YEAR1+") LEFT JOIN pqfData q2 ON "+
				"(q2.conID=accounts.id AND q2.questionID="+com.picsauditing.PICS.pqf.Constants.EMR_YEAR2+
				") LEFT JOIN pqfData q3 ON (q3.conID=accounts.id AND q3.questionID="+
				com.picsauditing.PICS.pqf.Constants.EMR_YEAR3+") ";
		}//if
		if ("Contractor".equals(searchType))
			joinQuery+="INNER JOIN contractor_info ON (accounts.id=contractor_info.id) ";
		if (accessType.equals("Auditor")) {
			if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(selected_auditType))
				whereQuery += "AND contractor_info.auditor_id="+accessID+" ";
			else if (accessType.equals("Auditor") && com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(selected_auditType))
				whereQuery += "AND contractor_info.desktopAuditor_id="+accessID+" ";
			else if (accessType.equals("Auditor") && com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(selected_auditType))
				whereQuery += "AND contractor_info.daAuditor_id="+accessID+" ";
			else if (accessType.equals("Auditor") && com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(selected_auditType))
				whereQuery += "AND contractor_info.pqfAuditor_id="+accessID+" ";
			else
				whereQuery += "AND 0 ";
		}//if
		if ("Operator".equals(accessType)) {
//		else if (AccountBean.ALL_ACCESS !-accessID == !.equals("Admin")) {
			joinQuery += "INNER JOIN generalContractors gc ON gc.subID=accounts.id "+
				"LEFT JOIN flags ON flags.conID=accounts.id ";
			whereQuery += "AND gc.genID="+accessID+" AND (flags.opID IS NULL OR flags.opID="+accessID+") ";
		}//if
		if ("Corporate".equals(accessType)) {
			joinQuery += "INNER JOIN generalContractors gc ON gc.subID=accounts.id "+
					"LEFT JOIN flags ON flags.conID=accounts.id ";
			whereQuery+="AND gc.genID IN "+permissions.oBean.getFacilitiesSet()+" "+
					"AND (flags.opID IS NULL OR flags.opID="+accessID+") ";
			groupByQuery = "GROUP BY accounts.id ";
		}//if

		Conn = DBBean.getDBConnection();
		SQLStatement = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		count = beginResults = (showPage-1)*showNum;
		Query = "SELECT SQL_CALC_FOUND_ROWS * FROM accounts "+joinQuery+pqfJoinQuery+oshaJoinQuery+ncmsJoinQuery+
				"WHERE 1 "+whereQuery+groupByQuery+"ORDER BY "+orderBy+" LIMIT "+count+","+showNum+";";
		if ("Corporate".equals(accessType))
			Query = "SELECT SQL_CALC_FOUND_ROWS * FROM accounts "+joinQuery+pqfJoinQuery+oshaJoinQuery+ncmsJoinQuery+
					"WHERE 1 "+whereQuery+groupByQuery+"ORDER BY "+orderBy+" LIMIT "+count+","+showNum+";";

		//System.out.println(Query);
		SQLResult = SQLStatement.executeQuery(Query);

		ResultSet tempRS = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
				.executeQuery("SELECT FOUND_ROWS();");
		tempRS.next();
		numResults = tempRS.getInt(1);

		count++;
		beginResults = (showPage-1)*showNum+1;
		endResults = showPage*showNum;
		if (numResults == 0) {
			beginResults = 0;
			endResults = 0;
		}//if
		if (endResults > numResults)
			endResults = numResults;
		thisPage = r.getContextPath() + r.getServletPath();
	}//doSearch
	
	public String searchIncidenceRate = "";
	public String searchEMRRate = "";
	public String selected_searchYear1 = "";
	public String selected_searchYear2 = "";
	public String selected_searchYear3 = "";
	public boolean isFatalitiesReport = false;

	public void setIsFatalitiesReport() {
		isFatalitiesReport = true;
		isOSHASearch = true;
	}//setIsFatalitiesReport

	public void setIsHurdleRatesReport() {
		isHurdleRatesReport = true;	
		isOSHASearch = true;
	}//setIsHurdleRatesReport

	public void setIsEMRRatesReport() {
		isEMRRatesReport = true;	
	}//setIsEMRRatesReport

	public void setIsOSHASearch() {
		isOSHASearch = true;
	}//setIsOSHASearch

	public void setIsDesktopReport() {
		isDesktopReport = true;
	}//setIsDesktopSearch

	public String getTextColor() throws Exception {
		if (!canSeeContractor())
			return "cantSee";
		return cBean.getTextColor();
	}//getTextColor

	public String getActiveStar() {
		if ("N".equals(aBean.active))
			return "*";
		return "";
	}//getActiveStar

	public String getLinks(){
		int SHOW_PAGES = 4;
		int lastPage = (numResults-1)/showNum+1;
		String orderByQuery = "";
		if (null != orderBy && !"".equals(orderBy))
			orderByQuery = "&orderBy="+orderBy;
		String temp = "<span class=\"redMain\">";
		temp+="Showing "+beginResults+"-"+endResults+" of <b>"+numResults+"</b> results | ";
		int startIndex = 1;
		if (showPage-1 > SHOW_PAGES){
			startIndex = showPage-SHOW_PAGES;
			temp+="<a href=\""+thisPage+"?changed=0"+orderByQuery+"&showPage=1\">1</A> << ";
		}//if
		int endIndex = lastPage;
		if (lastPage-showPage > SHOW_PAGES)
			endIndex = showPage+SHOW_PAGES;
		for (int i=startIndex;i<=endIndex;i++){
			if (i==showPage)
				temp+=" <strong>"+i+"</strong> ";
			else{
				temp+="<a href=\""+thisPage+"?changed=0"+orderByQuery+"&showPage="+i+"\">"+i+"</A> ";
			}//else
		}//for
		if (lastPage-showPage > SHOW_PAGES)
			temp+=" >> <a href=\""+thisPage+"?changed=0"+orderByQuery+"&showPage="+lastPage+"\">"+lastPage+"</A> ";
		temp+="</span>";
		return temp;
	}//getLinks

	public String getStartsWithLinks() {
		String temp = "<span class=\"blueMain\">Starts with: ";
		for (char c = 'A';c<='Z';c++)
			temp += "<a href="+thisPage+"?startsWith="+c+"&changed=1 class=blueMain>"+c+"</a> ";
		temp += "</span>";
		return temp;
	}//getStartsWithLinks

	public static String getAccountsManageStartsWithLinks() {
		String temp = "<span class=\"blueMain\">Starts with: ";
		for (char c = 'A';c<='Z';c++)
			temp += "<a href=accounts_manage.jsp?startsWith="+c+"&changed=1 class=blueMain>"+c+"</a> ";
		temp +="</span>";
		return temp;
	}//getAccountsManageStartsWithLinks

	public boolean isNextRecord() throws Exception {
		if (!(count <= endResults && SQLResult.next()))
			return false;
		count++;
		aBean.setFromResultSet(SQLResult);
		if ("Contractor".equals(searchType))
			cBean.setFromResultSet(SQLResult);
		if (isOSHASearch)
			osBean.setFromResultSet(SQLResult);
		if (isDesktopReport){
			manualRevisionDate = DateBean.toShowFormat(SQLResult.getString("manRevisionQ.answer"));
			if (null == manualRevisionDate)
				manualRevisionDate = "";
		}//if
		if (isHurdleRatesReport || isEMRRatesReport){
			emr1 = Utilities.convertPercentToDecimal(SQLResult.getString("q1.verifiedAnswer"));
			if ("0.0".equals(emr1))
				emr1 = Utilities.convertPercentToDecimal(SQLResult.getString("q1.answer"));
			emr2 = Utilities.convertPercentToDecimal(SQLResult.getString("q2.verifiedAnswer"));
			if ("0.0".equals(emr2))
				emr2 = Utilities.convertPercentToDecimal(SQLResult.getString("q2.answer"));
			emr3 = Utilities.convertPercentToDecimal(SQLResult.getString("q3.verifiedAnswer"));
			if ("0.0".equals(emr3))
				emr3 = Utilities.convertPercentToDecimal(SQLResult.getString("q3.answer"));
			float temp1 = Float.parseFloat(emr1);
			float temp2 = Float.parseFloat(emr2);
			float temp3 = Float.parseFloat(emr3);
			java.text.DecimalFormat decFormatter = new java.text.DecimalFormat("###,##0.00");
			emrAve = decFormatter.format((temp1+temp2+temp3)/3);
			if(isHurdleRatesReport){
				q318 = SQLResult.getString("q318.answer");
				q1385 = SQLResult.getString("q1385.answer");
			}//if
		}//if
		if (!DEFAULT_TRADE.equals(selected_trade)){
			String temp = SQLResult.getString("tradeQ.answer");
			tradePerformedBy = "";
			if (temp.equals("C "))
				tradePerformedBy = "Self Performed";
			else if (temp.equals(" S"))
				tradePerformedBy = "Sub Contracted";
			else if (temp.equals("C S"))
				tradePerformedBy = "Self Performed, Sub Contracted";
		}//if
		return true;
	}//isNextRecord
	
	public boolean isNextRecord(IPicsDO domObj) throws Exception {
		if (!(count <= endResults && SQLResult.next()))
			return false;
		count++;
		domObj.setFromResultSet(SQLResult);
		return true;
	}
		
	public void closeSearch() throws Exception {
		numResults = 0;
		count = 0;
		if (null != SQLResult) {
			SQLResult.close();
			SQLResult = null;
		}//if
		if (null != SQLStatement) {
			SQLStatement.close();
			SQLStatement = null;
		}//if
		if (null != Conn) {
			Conn.close();
			Conn = null;
		}//if
		// reset some of the set search parameters because the search Bean is a session object and persists from page to page		
		expiresInDays = "";
//		prequalExpiresInDays = "";
//		incompleteAfter = "";
		isFatalitiesReport = false;
//		isAuditorsReport = false;
//		isAnnualUpdateReport = false;
		isPaymentReport = false;
		isUpgradePaymentReport = false; 
		isActivationReport = false;
		isNoInsuranceOnly = false;
		isOSHASearch = false;
		isNCMSReport = false;
		isHurdleRatesReport = false;
		isEMRRatesReport = false;
		isDesktopReport = false;
		searchType=DEFAULT_TYPE;
		//scheduleAuditsReport = false;
		whichScheduleAuditsReport = "";
	}//closeSearch
	
	public static String getSearchIndustrySelect(String name, String classType, String selectedIndustry) throws Exception {
		return Inputs.inputSelect(name, classType, selectedIndustry, INDUSTRY_SEARCH_ARRAY);
	}//getSearchIndustrySelect

	public static String getSearchZipSelect(String name, String classType, String selectedZip) throws Exception {
		return Inputs.inputSelect(name, classType, selectedZip, ZIP_SEARCH_ARRAY);
	}//getSearchZipSelect

	public static String getStatusSelect(String name, String classType, String selectedStatus) throws Exception {
		return Inputs.inputSelectFirst(name, classType, selectedStatus, ContractorBean.STATUS_ARRAY, DEFAULT_STATUS);
	}//getStatusSelect

	public static String getAuditStatusSelect(String name, String classType, String selectedAuditStatus) throws Exception {
		return Inputs.inputSelectFirst(name, classType, selectedAuditStatus, ContractorBean.AUDIT_STATUS_ARRAY, DEFAULT_AUDIT_STATUS);
	}//getAuditStatusSelect
	
	public static String getStateSelect(String name, String classType, String selectedState) throws Exception {
		return Inputs.inputSelect2First(name, classType, selectedState, Inputs.STATE_ARRAY, "",DEFAULT_STATE);
	}//getStateSelect

	public static String getSearchGeneralSelect(String name, String classType, String selectedGeneral) throws Exception {
		return new AccountBean().getGeneralSelect2(name, classType, selectedGeneral, LIST_DEFAULT);
	}//getSearchOperatorSelect

	public void deleteAccount(String delete_id, String path) throws Exception {
		aBean.deleteAccount(delete_id, path);
	}//deleteAccount

	public void setHasCertSet(HashSet temp) {
		hasCertSet = temp;
	}//setHasCertSet

	public void setCanSeeSet(HashSet temp) {
		canSeeSet = temp;
	}//setCanSeeSet

	public void setAuditorCanSeeSet(HashSet temp) {
		auditorCanSeeSet = temp;
	}//setAuditorCanSeeSet
	
	public boolean canSeeContractor() {
		return (null == canSeeSet || canSeeSet.contains(aBean.id));
	}//cantSeeContractor

	public void setExpiresInDays(String s) {
		expiresInDays = s;
	}//setExpiresInDays

//	public void setIncompleteAfter(String s) {
//		incompleteAfter = s;
//	}//setIncompleteAfter

//	public void setPrequalExpiresInDays(String s) {
//		prequalExpiresInDays = s;
//	}//setPrequalExpiresInDays

//	jj 7-7-06
//	public void setIsAnnualUpdateReport() {
//		isAnnualUpdateReport = true;
//	}//isAnnualUpdateReport
	
	public void setIsPaymentReport(boolean tempShowAll) {
		isPaymentReport = true;
		showAll = tempShowAll;
	}//isPaymentReport

	public void setIsActivationReport() {
		isActivationReport = true;
	}//isActivationReport

	public void setNoInsuranceOnly() {
		isNoInsuranceOnly = true;
	}//setNoInsuranceOnly

	public void setWhichScheduleAuditsReport(String whichAudits) {
		whichScheduleAuditsReport = whichAudits;
	}//setWhichScheduleAuditsReport

	public void writeToExcelFile(javax.servlet.ServletConfig config, PermissionsBean pBean) throws Exception {
		Connection excelConn = null;
		Statement excelSQLStatement = null;
		ResultSet excelSQLResult = null;

		try{
			String excelQuery = Query.substring(0, Query.indexOf("LIMIT"));
			excelConn = DBBean.getDBConnection();
			excelSQLStatement = excelConn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			excelSQLResult = excelSQLStatement.executeQuery(excelQuery);
			
			if ("28".equals(pBean.userID)){
				ExcelWriterBean ewBean = new ExcelWriterBean();
				ewBean.writeCPReport(config, excelSQLResult, pBean.userID);
			}//if
			else {
				ExcelWriterBean ewBean = new ExcelWriterBean();
				ExcelWriterBean.init(config);
				ewBean.openTempExcelFile(pBean.userID);
				ewBean.writeHeaders(pBean);
				excelSQLResult.beforeFirst();
				while (excelSQLResult.next()){
					aBean.setFromResultSet(excelSQLResult);
					cBean.setFromResultSet(excelSQLResult);
					ewBean.writeLine(aBean,cBean,pBean);
				}//while
				ewBean.setColumnLengths(pBean);
				ewBean.closeTempExcelFile();
			}//else
		}finally{
			if (null != excelConn){
				excelConn.close();
				excelConn = null;
			}//if
			if (null != excelSQLStatement){
				excelSQLStatement.close();
				excelSQLStatement = null;
			}//if
			if (null != excelSQLResult){
				excelSQLResult.close();
				excelSQLResult = null;		
			}//if
		}//finally
	}//writeToExcelFile

	public String getExcelLink(String id) throws Exception {
		return ExcelWriterBean.getLink(id);
	}//getExcelLink

	public String getPQFAnswer() throws Exception {
		if ("".equals(searchEMRRate))
			return "";
		else
			return SQLResult.getString("verifiedAnswer");
	}//getPQFAnswer

	public String getPQFQuestionID() throws Exception {
		if ("".equals(searchEMRRate))
			return "";
		else
			return SQLResult.getString("questionID");
	}//getPQFAnswer

	public String getListLink(String auditType) throws Exception {
		if (!canSeeContractor())
			return "";
		if (cBean.isExempt() && !com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType))
			return "N/A";
		if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) && cBean.isPQFSubmitted())
			return "<a href=/pqf_view.jsp?id="+aBean.id+"&auditType="+auditType+
				"><img src=images/icon_"+auditType+".gif width=20 height=20 border=0></a>";
		if (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) && cBean.isDaSubmitted())
			return "<a href=/pqf_view.jsp?id="+aBean.id+"&auditType="+auditType+
			"><img src=images/icon_"+auditType+".gif width=20 height=20 border=0></a>";
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && cBean.isDesktopStatusOldAuditStatus())
			return getListLink(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE);
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && cBean.isDesktopSubmitted())
			return "<a href=/pqf_view.jsp?id="+aBean.id+"&auditType="+auditType+
				"><img src=images/icon_"+auditType+".gif width=20 height=20 border=0></a>";
		if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType) && cBean.isAuditCompleted())
			if (cBean.isNewOfficeAudit())
				return "<a href=/pqf_view.jsp?id="+aBean.id+"&auditType="+auditType+
					"><img src=images/icon_"+auditType+".gif width=20 height=20 border=0></a>";			
			else
				return "<a href=/audit_view.jsp?id="+aBean.id+"><img src=images/icon_"+auditType+".gif width=20 height=20 border=0></a>";
		return "<img src=images/notOkCheck.gif width=19 height=15 alt='Not Complete'>";
	}//getListLink

	public String getPercentCompleteLink(String auditType) throws Exception {
		if (!canSeeContractor())
			return "";
		String percent = "";
		if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType))
			percent = cBean.desktopVerifiedPercent;
		if (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType))
			percent = cBean.daVerifiedPercent;
		else if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType))
			percent = cBean.officeVerifiedPercent;
		else if (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType)){
			percent = cBean.getPercentComplete(auditType);
			if (ContractorBean.AUDIT_STATUS_CLOSED.equals(cBean.calcPQFStatus()))
				percent = "100";
		} else
			percent = cBean.getPercentComplete(auditType);
		if ("100".equals(percent))
			return "<a href=/pqf_view.jsp?id="+aBean.id+"&auditType="+auditType+
				"><img src=images/icon_"+auditType+".gif width=20 height=20 border=0></a>";
		else
			return "<a class="+getTextColor()+" href=/pqf_view.jsp?id="+aBean.id+"&auditType="+auditType+">"+percent+"%</a>";
	}//getPercentCompleteLink
	
	public void pageResults(ResultSet sqlResult, int resultsOnPage, javax.servlet.http.HttpServletRequest r ) throws Exception{
		
		SQLResult = sqlResult; 
		showNum = resultsOnPage;
		
		changed = r.getParameter("changed");
		if ((null == changed) || ("1".equals(changed))) {
			showPage = 1;
		}else
			showPage = Integer.parseInt(r.getParameter("showPage"));
		
		while (SQLResult.next()) 
			numResults++;
			
		count = 1;
		SQLResult.beforeFirst();
	
		beginResults = (showPage-1)*showNum+1;
		
		endResults = showPage*showNum;
		if (numResults == 0) {
			beginResults = 0;
			endResults = 0;
		}//if
		if (endResults > numResults)
			endResults = numResults;
		while (count < beginResults) {
			count++;
			SQLResult.next();
		}//while
	
		thisPage = r.getContextPath() + r.getServletPath();
	}

	public String getFlagLink() throws Exception {
		String flag = SQLResult.getString("flag");
		if (null == flag)
			return "";
		return "<img src=images/icon_"+flag.toLowerCase()+"Flag.gif width=12 height=15 border=0>";
	}//getFlagLink
}//SearchBean