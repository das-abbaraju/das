package com.picsauditing.PICS;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import com.picsauditing.access.OpPerms;
import com.picsauditing.domain.IPicsDO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.LinkBuilder;

/**
 * @author Jeff Jensen
 *
 */
public class SearchBean {
	public AccountBean aBean = new AccountBean();
	public ContractorBean cBean = new ContractorBean();
	public OperatorBean oBean = new OperatorBean();

	public String emr1 = "";
	public String emr2 = "";
	public String emr3 = "";
	public String emrAve = "";
	public String q318 = "";
	public String q1385 = "";
	public String manualRevisionDate = "";
	public String tradePerformedBy = "";

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
	public String selected_worksIn = "";
	public String selected_officeIn = "";
	
	public String expiresInDays = "";
	public String searchType = DEFAULT_TYPE;
	public boolean isPaymentReport = false;
	public boolean isUpgradePaymentReport = false;
	public boolean isActivationReport = false;
	public boolean isNoInsuranceOnly = false;
	
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
	public static final String DEFAULT_LICENSED_IN_ID = "0";
	public static final String DEFAULT_WORKS_IN = "- Works In -";
	public static final String DEFAULT_OFFICE_IN = "- Office Location -";
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
	public static final String[] INDUSTRY_SEARCH_ARRAY = {DEFAULT_INDUSTRY,"Petrochemical","Mining","Power","General",
											"Construction","Manufacturing","Pharmaceutical","Telecommunications"};		
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
	}

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

		// We aren't caching the SearchBean in the session anymore, so we always need to rerun the search
		// Added this back in
		if ((null == changed) || ("1".equals(changed))) {
// if it's a new search, reset all the search parameters
			showPage = 1;
			try {
				showPage = Integer.parseInt(r.getParameter("showPage"));
			} catch (Exception e) {}
			
			selected_name = r.getParameter("name");
			selected_industry = r.getParameter("industry");
			selected_trade = r.getParameter("trade");
			selected_startsWith = r.getParameter("startsWith");
			selected_zip = r.getParameter("zip");
			selected_city = r.getParameter("city");
			selected_state = r.getParameter("state");
			selected_status = r.getParameter("status");
			selected_auditStatus = r.getParameter("auditStatus");
			if (r.getParameter("auditType") != null)
				throw new Exception("auditType is set but this code was removed");
			// temporary check to make sure we didn't clean the code too much
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
			selected_worksIn = r.getParameter("worksIn");
			selected_officeIn = r.getParameter("officeIn");
			
			selected_flagStatus = r.getParameter("flagStatus");
			selected_taxID = r.getParameter("taxID");
			selected_performedBy = r.getParameter("performedBy");
		}//if
		else {
			try {
				showPage = Integer.parseInt(r.getParameter("showPage"));
			} catch (Exception e) {
				showPage = 1;
			}
		}

		if ("Y".equals(selected_entireDB)){
			joinQuery += "LEFT JOIN flags ON flags.conID=accounts.id AND flags.opID="+accessID+" ";
			if ("Y".equals(searchCorporate)) {
				if (permissions.isOperator())
					whereQuery += "AND accounts.id IN (SELECT subID FROM generalcontractors gc " +
							"JOIN facilities f ON gc.genID = f.opID " +
							"JOIN facilities myf ON f.corporateID = myf.corporateID AND myf.opID = " + accessID + ") ";
				if (permissions.isCorporate())
					whereQuery += "AND accounts.id IN (SELECT subID FROM generalcontractors gc " +
					"JOIN facilities f ON gc.genID = f.opID AND f.corporateID = " + accessID + ") ";
			}
			accessType = "Admin";
		}//if
		//Set all the Queries
		if (onlyActive)
			whereQuery += "AND active='Y' ";
		if (null==searchType)
			searchType = DEFAULT_TYPE;
		whereQuery += "AND type='"+searchType+"' ";
		if (!isSet(selected_name, DEFAULT_NAME))
			selected_name = DEFAULT_NAME;
		else
			whereQuery += "AND name LIKE '%"+Utilities.escapeQuotes(selected_name)+"%' ";
		if (!isSet(selected_industry, DEFAULT_INDUSTRY))
			selected_industry = DEFAULT_INDUSTRY;
		else
			whereQuery += "AND industry='"+selected_industry+"' ";
		if (!isSet(selected_trade, DEFAULT_TRADE))
			selected_trade = DEFAULT_TRADE;
		else {
			whereQuery += "AND (tradeQ.questionID='"+selected_trade+"' ";
			joinQuery +="JOIN contractor_audit ca ON (ca.conID = accounts.id)";
			joinQuery += "INNER JOIN pqfData tradeQ ON (tradeQ.auditID = ca.auditID) ";
			
			if (!isSet(selected_performedBy, DEFAULT_PERFORMED_BY)) {
				selected_performedBy = DEFAULT_PERFORMED_BY;
				whereQuery += "AND tradeQ.answer<>'') ";
			} else {
				if ("Sub Contracted".equals(selected_performedBy))
					whereQuery += "AND tradeQ.answer IN (' S','C S')) ";
				else if	("Self Performed".equals(selected_performedBy))
					whereQuery += "AND tradeQ.answer IN ('C  ','C S')) ";
			}
		}
		
		if (!isSet(selected_stateLicensedIn, DEFAULT_LICENSED_IN_ID))
			selected_stateLicensedIn = DEFAULT_LICENSED_IN_ID;
		else {
			whereQuery += "AND (licensedInQ.questionID='"+selected_stateLicensedIn+"' AND licensedInQ.answer<>'') ";
			joinQuery +="JOIN contractor_audit ca ON (ca.conID = accounts.id)";
			joinQuery += "INNER JOIN pqfData licensedInQ ON (licensedInQ.auditID = ca.auditID) ";
		}
		if (!isSet(selected_worksIn, DEFAULT_LICENSED_IN_ID))
			selected_worksIn = DEFAULT_LICENSED_IN_ID;
		else {
			whereQuery += "AND (worksInQ.questionID='"+selected_worksIn+"' AND worksInQ.answer<>'No') ";
			joinQuery +="JOIN contractor_audit ca ON (ca.auditID = accounts.id)";
			joinQuery += "INNER JOIN pqfData worksInQ ON (worksInQ.auditID = ca.auditID)";
		}
		if (!isSet(selected_officeIn, DEFAULT_LICENSED_IN_ID))
			selected_officeIn =  DEFAULT_LICENSED_IN_ID;
		else {
			whereQuery += "AND (officeInQ.questionID='"+selected_officeIn+"' AND officeInQ.answer LIKE '%Office') ";
			joinQuery +="JOIN contractor_audit ca ON (ca.auditID = accounts.id)";
			joinQuery += "INNER JOIN pqfData officeInQ ON (officeInQ.auditID = ca.auditID) ";
		}
			
		
		if (!isSet(selected_flagStatus, DEFAULT_FLAG_STATUS))
			selected_flagStatus = DEFAULT_FLAG_STATUS;
		else
			whereQuery += "AND flags.flag='"+selected_flagStatus+"' ";
		if (!isSet(selected_taxID, DEFAULT_TAX_ID))
			selected_taxID = DEFAULT_TAX_ID;
		else
			whereQuery += "AND taxID='"+selected_taxID+"' ";		
		if (isSet(selected_startsWith, ""))
			whereQuery += "AND name LIKE '"+Utilities.escapeQuotes(selected_startsWith)+"%' ";
		if (!isSet(selected_zip, DEFAULT_ZIP))
			selected_zip = DEFAULT_ZIP;
		else
			whereQuery += "AND zip LIKE '"+Utilities.escapeQuotes(selected_zip)+"%' ";
		if (!isSet(selected_city, DEFAULT_CITY))
			selected_city = DEFAULT_CITY;
		else
			whereQuery += "AND city LIKE '%"+Utilities.escapeQuotes(selected_city)+"%' ";
		if (!isSet(selected_state, DEFAULT_STATE))
			selected_state = DEFAULT_STATE;
		else
			whereQuery += "AND state='"+selected_state+"' ";
		if (isSet(selected_generalContractorID, DEFAULT_GENERAL_VALUE)) {
			accessID = selected_generalContractorID;
			accessType = "Operator";
		}
		if (!isSet(selected_status, DEFAULT_STATUS))
			selected_status = DEFAULT_STATUS;
		else{
			if ("Active".equals(selected_status))
				whereQuery+="AND (1 ";
			else
				whereQuery+="AND !(1 ";
			if (permissions.getPermissions().canSeeAudit(AuditType.PQF))
				whereQuery += "AND (pqfSubmittedDate<>'0000-00-00' AND pqfSubmittedDate>'"+DateBean.PQF_EXPIRED_CUTOFF+"') ";
			whereQuery += "AND (isExempt='Yes' OR (1 ";
			if (permissions.getPermissions().canSeeAudit(AuditType.DESKTOP))
				whereQuery += "AND desktopValidUntilDate>=CURDATE() ";
			if (permissions.getPermissions().canSeeAudit(AuditType.DA))
				whereQuery += "AND (daSubmittedDate<>'0000-00-00' AND daSubmittedDate>DATE_ADD(CURDATE(),INTERVAL -3 YEAR) "+
					"AND daClosedDate<>'0000-00-00') ";
			if (permissions.getPermissions().canSeeAudit(AuditType.OFFICE))
				whereQuery += "AND auditValidUntilDate>=CURDATE() ";
			whereQuery+="))) ";
//********************
		}//else
		if (!isSet(selected_auditStatus, DEFAULT_AUDIT_STATUS))
			selected_auditStatus = DEFAULT_AUDIT_STATUS;
		else
			whereQuery += "AND auditStatus='"+selected_auditStatus+"' ";
		if (!isSet(selected_pqfAuditorID, DEFAULT_AUDITOR_ID))
			selected_pqfAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND pqfAuditor_id="+selected_pqfAuditorID+" ";
		if (!isSet(selected_desktopAuditorID, DEFAULT_AUDITOR_ID))
			selected_desktopAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND desktopAuditor_id="+selected_desktopAuditorID+" ";
		if (!isSet(selected_daAuditorID, DEFAULT_AUDITOR_ID))
			selected_daAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND daAuditor_id="+selected_daAuditorID+" ";
		if (!isSet(selected_officeAuditorID, DEFAULT_AUDITOR_ID))
			selected_officeAuditorID = DEFAULT_AUDITOR_ID;
		else
			whereQuery += "AND auditor_id="+selected_officeAuditorID+" ";
		if (ONLY_CERTS.equals(selected_certsOnly))
			whereQuery += "AND isOnlyCerts='Yes' ";
		if (EXCLUDE_CERTS.equals(selected_certsOnly))
			whereQuery += "AND isOnlyCerts<>'Yes' ";
		if ("Y".equals(searchCorporate))
            accessType = "Corporate";
        if (isActivationReport) {
    		whereQuery += "AND accounts.id IN (SELECT id FROM contractor_info WHERE accountDate='0000-00-00' OR accountDate IS NULL) ";
        }
		if (isNoInsuranceOnly)
			whereQuery += "AND isOnlyCerts='No' ";
		//for incomplete audit report
		if (!"".equals(selected_incompleteAfter) && 
				(permissions.oBean.canSeeDesktop() || permissions.oBean.canSeeDA() || permissions.oBean.canSeeOffice() || permissions.isAdmin())){
			whereQuery +="AND (";
			if (permissions.oBean.canSeeDesktop() || permissions.isAdmin())
				whereQuery += "((desktopSubmittedDate<>'0000-00-00' AND desktopSubmittedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) "+"AND desktopClosedDate='0000-00-00') OR (desktopSubmittedDate='0000-00-00' AND "+
						"auditCompletedDate<>'0000-00-00' AND "+
						"auditCompletedDate<'"+DateBean.OLD_OFFICE_CUTOFF+"' AND auditCompletedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) AND auditClosedDate='0000-00-00')) ";
			if (permissions.oBean.canSeeDesktop() && permissions.oBean.canSeeDA() || permissions.isAdmin())
				whereQuery +=" OR ";
			if (permissions.oBean.canSeeDA() || permissions.isAdmin())
				whereQuery += "(daSubmittedDate<>'0000-00-00' AND daSubmittedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) AND daClosedDate='0000-00-00')";
			if ((permissions.oBean.canSeeDesktop() || permissions.oBean.canSeeDA())
						&& permissions.oBean.canSeeOffice() || permissions.isAdmin())
				whereQuery +=" OR ";
			if (permissions.oBean.canSeeOffice() || permissions.isAdmin())
				whereQuery += "(auditCompletedDate<>'0000-00-00' AND auditCompletedDate<DATE_ADD(CURDATE(),INTERVAL -"+selected_incompleteAfter+
						" MONTH) AND auditClosedDate='0000-00-00')";
			whereQuery +=")";
		}//if
		if (!isSet(selected_invoicedStatus, DEFAULT_INVOICED_STATUS))
			selected_invoicedStatus = DEFAULT_INVOICED_STATUS;
		else if ("Invoiced".equals(selected_invoicedStatus))
			whereQuery += "AND billingAmount=newBillingAmount ";
		else if ("Not Invoiced".equals(selected_invoicedStatus))
			whereQuery += "AND billingAmount<>newBillingAmount ";
		if (!isSet(selected_auditLocation, DEFAULT_AUDIT_LOCATION))
			selected_auditLocation = DEFAULT_AUDIT_LOCATION;
		else
			whereQuery += "AND auditLocation='"+selected_auditLocation+"' ";
		if (!isSet(selected_visible, DEFAULT_VISIBLE))
			selected_visible = DEFAULT_VISIBLE;
		else
			whereQuery += "AND active='"+selected_visible+"' ";
		if (!"".equals(expiresInDays))
			whereQuery += "AND (auditCompletedDate <> '0000-00-00' AND DATE_ADD(auditCompletedDate,INTERVAL 3 YEAR) < DATE_ADD(CURDATE(),INTERVAL "+
					expiresInDays+" DAY)) ";
		if (isPaymentReport && !showAll)
			whereQuery += "AND mustPay='Yes' AND (paymentExpires='0000-00-00' OR DATE_ADD(CURDATE(),INTERVAL 35 DAY)>paymentExpires) ";
		if (isPaymentReport && showAll)
			whereQuery += "AND mustPay='Yes'";
		if (isUpgradePaymentReport)
			whereQuery += "AND (lastPaymentAmount<newBillingAmount OR (billingCycle>1 AND newbillingAmount>=799)) "+
					"AND mustPay='Yes' AND isExempt='No' AND (paymentExpires>DATE_ADD(CURDATE(),INTERVAL 90 DAY) "+
					"OR lastInvoiceDate>lastPayment) ";
		
		//OSHA Queries
		if (!"".equals(searchIncidenceRate)) {
			whereQuery += "AND (0 ";
			if (isSet(selected_searchYear1, ""))
				whereQuery += "OR ((recordableTotal1*200000)/manHours1 >= "+searchIncidenceRate+") ";
			if (isSet(selected_searchYear2, ""))
				whereQuery += "OR ((recordableTotal2*200000)/manHours2 >= "+searchIncidenceRate+") ";
			if (isSet(selected_searchYear3, ""))
				whereQuery += "OR ((recordableTotal3*200000)/manHours3 >= "+searchIncidenceRate+") ";
			whereQuery += ") ";
		}//if
		if (isFatalitiesReport)
			whereQuery += "AND (fatalities1>0 OR fatalities2>0 OR fatalities3>0) ";
		if ("Contractor".equals(searchType))
			joinQuery+="INNER JOIN contractor_info ON (accounts.id=contractor_info.id) ";
		
		if ("Operator".equals(accessType)) {
			boolean hideUnApproved = permissions.oBean.isApprovesRelationships() && !permissions.getPermissions().hasPermission(OpPerms.ViewUnApproved);
			
			joinQuery += "JOIN generalContractors gc ON gc.subID=accounts.id AND gc.genID="+accessID+ (hideUnApproved?" AND gc.workStatus = 'Y' ": " ")+
				" LEFT JOIN flags ON flags.conID=accounts.id AND flags.opID="+accessID+" ";
		}
		if ("Corporate".equals(accessType)) {
			joinQuery += "INNER JOIN generalContractors gc ON gc.subID=accounts.id ";
			whereQuery+="AND gc.genID IN "+permissions.oBean.getFacilitiesSet()+" ";
			groupByQuery = "GROUP BY accounts.id ";
		}

		Conn = DBBean.getDBConnection();
		SQLStatement = Conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		count = beginResults = (showPage-1)*showNum;
		
		
		//////////////////////////////////////
		// Construct the SQL statement now
		Query = "SELECT SQL_CALC_FOUND_ROWS * FROM accounts "+joinQuery+pqfJoinQuery+oshaJoinQuery+ncmsJoinQuery+
				"WHERE 1 "+whereQuery+groupByQuery+"ORDER BY "+orderBy+" LIMIT "+count+","+showNum+";";

		System.out.println(Query);
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
	
	public boolean isSet(Object value, Object defaultValue) {
		if (value == null) return false;
		if (value.toString().length() == 0) return false;
		if (value.equals(defaultValue)) return false;
		return true;
	}
	
	public String searchIncidenceRate = "";
	public String searchEMRRate = "";
	public String selected_searchYear1 = "";
	public String selected_searchYear2 = "";
	public String selected_searchYear3 = "";
	public boolean isFatalitiesReport = false;

	public void setIsFatalitiesReport() {
		isFatalitiesReport = true;
	}

	@Deprecated
	public String getTextColor() throws Exception {
		return "";
	}

	public String getActiveStar() {
		if ("N".equals(aBean.active))
			return "*";
		return "";
	}

	public String getLinks(){
		return getLinks("");
	}
	public String getLinks(String filter){
		int SHOW_PAGES = 4;
		int lastPage = (numResults-1)/showNum+1;
		String orderByQuery = "";
		if (null != orderBy && !"".equals(orderBy) && !filter.contains("orderBy"))
			orderByQuery = "orderBy="+orderBy;
		String temp = "<span class=\"redMain\">";
		temp+="Showing "+beginResults+"-"+endResults+" of <b>"+numResults+"</b> results | ";
		
		int startIndex = 1;
		if (showPage-1 > SHOW_PAGES){
			startIndex = showPage-SHOW_PAGES;
			temp+="<a href=\""+thisPage+"?"+orderByQuery+filter+"\">1</A> << ";
		}
		
		int endIndex = lastPage;
		if (lastPage-showPage > SHOW_PAGES)
			endIndex = showPage+SHOW_PAGES;
		for (int i=startIndex;i<=endIndex;i++){
			if (i==showPage)
				temp+=" <strong>"+i+"</strong> ";
			else{
				temp+="<a href=\""+thisPage+"?"+orderByQuery+filter+"&showPage="+i+"\">"+i+"</A> ";
			}
		}
		
		if (lastPage-showPage > SHOW_PAGES)
			temp+=" >> <a href=\""+thisPage+"?"+orderByQuery+filter+"&showPage="+lastPage+"\">"+lastPage+"</A> ";
		temp+="</span>";
		return temp;
	}

	

	/**
	 * Once we push this to all pages and hammer out any problems, we'll get rid of it and 
	 * change/use getStartsWithLinks
	 * @return
	 */
	public String getLinksWithDynamicForm(){
		return LinkBuilder.getPageNOfXLinks(numResults, showNum, beginResults, endResults, showPage);
	}
	
	public String getStartsWithLinks() {
		String temp = "<span class=\"blueMain\">Starts with: ";
		for (char c = 'A';c<='Z';c++)
			temp += "<a href="+thisPage+"?startsWith="+c+"&changed=1 class=blueMain>"+c+"</a> ";
		temp += "</span>";
		return temp;
	}//getStartsWithLinks

	/**
	 * Once we push this to all pages and hammer out any problems, we'll get rid of it and 
	 * change/use getStartsWithLinks
	 * @return
	 */
	public String getStartsWithLinksWithDynamicForm() {
		return LinkBuilder.getStartsWithLinks();
	}
	
	public boolean isNextRecord() throws Exception {
		if (!(count <= endResults && SQLResult.next()))
			return false;
		count++;
		aBean.setFromResultSet(SQLResult);
		if ("Contractor".equals(searchType))
			cBean.setFromResultSet(SQLResult);
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
		isFatalitiesReport = false;
		isPaymentReport = false;
		isUpgradePaymentReport = false; 
		isActivationReport = false;
		isNoInsuranceOnly = false;
		searchType=DEFAULT_TYPE;
		whichScheduleAuditsReport = "";
	}
	
	public static String getSearchIndustrySelect(String name, String classType, String selectedIndustry) throws Exception {
		return Inputs.inputSelect(name, classType, selectedIndustry, INDUSTRY_SEARCH_ARRAY);
	}//getSearchIndustrySelect

	public static String getSearchZipSelect(String name, String classType, String selectedZip) throws Exception {
		return Inputs.inputSelect(name, classType, selectedZip, ZIP_SEARCH_ARRAY);
	}//getSearchZipSelect

	public static String getStateSelect(String name, String classType, String selectedState) throws Exception {
		return Inputs.inputSelect2First(name, classType, selectedState, Inputs.STATE_ARRAY, "",DEFAULT_STATE);
	}//getStateSelect

	public static String getSearchGeneralSelect(String name, String classType, String selectedGeneral) throws Exception {
		return new AccountBean().getGeneralSelect2(name, classType, selectedGeneral, LIST_DEFAULT);
	}//getSearchOperatorSelect

	public void setCanSeeSet(HashSet temp) {
		canSeeSet = temp;
	}

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

	public String getConWorkStatus() throws Exception {
		return SQLResult.getString("workStatus");
	}

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

	private String getLink(String icon, String url) {
		StringBuilder link = new StringBuilder();
		boolean hasURL = false;
		if (url != null && url.length() > 0) hasURL = true;
		
		if (hasURL)
			link.append("<a href=\"").append(url).append("\">");
		link.append("<img src=\"images/").append(icon).append(".gif\" width=\"20\" height=\"20\" border=\"0\">");
		if (hasURL)
			link.append("</a>");
		return link.toString();
	}

	public void pageResults(ResultSet sqlResult, int resultsOnPage, javax.servlet.http.HttpServletRequest r ) throws Exception{
		
		SQLResult = sqlResult; 
		showNum = resultsOnPage;
		
		changed = r.getParameter("changed");
		try {
			showPage = Integer.parseInt(r.getParameter("showPage"));
		} catch (Exception e) {
			showPage = 1;
		}
		
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
	
}