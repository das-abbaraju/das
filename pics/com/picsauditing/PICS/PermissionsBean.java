package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.UserAccess;


public class PermissionsBean extends DataBean {
/*	History
	8/29/05 jj - created to control access to pages according to account type (admin,auditor,contractor,operator) and restrict privileges (write, read, none)
*/
	private static final String ADMIN_TYPE = "Admin";
	private static final String AUDITOR_TYPE = "Auditor";
	private static final String CONTRACTOR_TYPE = "Contractor";
	private static final String CORPORATE_TYPE = "Corporate";
	private static final String OPERATOR_TYPE = "Operator";
	private static final String[] USER_TYPES = {ADMIN_TYPE,AUDITOR_TYPE,CONTRACTOR_TYPE,CORPORATE_TYPE,OPERATOR_TYPE};

	private static final String[] RESTRICTED_AUDIT_CATEGORIES = {"2","6"};

	public static final int FULL = 1;
	public static final int CON_EDIT = 2;
	public static final int OP_VIEW = 3;
	public static final int AUD_VIEW = 4;
	public static final int NOT_CON = 5;
	public static final int LOGGED_IN = 6;
	public static final int PQF_VIEW = 7;
	public static final int OP_EDIT = 8;
	public static final int BASIC = 9;
	
	// Temporary Groups for Users so we can be backwards compatible with USER_TYPES
	private static final int GROUP_ADMIN = 10;
	private static final int GROUP_AUDITOR = 11;
	//private static final int GROUP_CONTRACTOR = 12;

	public String userID = ""; // user's accountID
	public String userName = "";
	public String userType = "";
	public boolean seesAll = false;
	public HashSet<String> canSeeSet = null;
	public HashSet<String> auditorCanSeeSet = null;
	public HashSet<String> auditorOfficeSet = null;
	public HashSet<String> auditorDesktopSet = null;
	public HashSet<String> auditorDaSet = null;
	public HashSet<String> auditorPQFSet = null;
	public OperatorBean oBean = null;
	public UserBean uBean = null;
	public boolean isMainAccount = true;
	public ArrayList<String> allFacilitiesAL = null;

	public com.picsauditing.access.UserAccess userAccess = null;
	private Permissions permissions = null;
	
	

	public void setAllFacilitiesFromDB(String conID) throws Exception {
		allFacilitiesAL = new ArrayList<String>();
	    String selectQuery = "SELECT genID FROM generalContractors WHERE subID="+conID+";";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				allFacilitiesAL.add(SQLResult.getString("genID"));
			SQLResult.close();
			selectQuery = "SELECT DISTINCT f.corporateID FROM generalContractors gc INNER JOIN facilities f "+
				"ON (gc.subID ="+conID+" AND gc.genID=f.opID) ORDER BY f.opID";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				allFacilitiesAL.add(SQLResult.getString("corporateID"));
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setAllFacilitiesFromDB
	
	public boolean canSeeFullPQF = false;
	public boolean loggedIn = false;
	public String thisPageID = "";
 
	public void setUserID(String s) {userID = s;}//setUserID
	public void setUserName(String s) {userName = s;}//setUserName
	public void setUserType(String s) {userType = s;}//setUserType
	public void setSeesAll(String s) {seesAll = "Y".equals(s);}//setSeesAll
	public void setCanSeeSet(HashSet<String> s) {canSeeSet = s;}//setSeesAll
	public void setAuditorCanSeeSet(HashSet<String> s) {auditorCanSeeSet = s;} 
	public void setAuditorPQFSet(HashSet<String> s) {auditorPQFSet = s;} 
	public void setAuditorOfficeSet(HashSet<String> s) {auditorOfficeSet = s;} 
	public void setAuditorDesktopSet(HashSet<String> s) {auditorDesktopSet = s;}
	public void setAuditorDaSet(HashSet<String> s) {auditorDaSet = s;}

	public void setUserAccess(String userID) throws Exception{
		userAccess = new com.picsauditing.access.UserAccess();
		//userAccess.setFromDB(userID);
		userAccess.setPermissions(permissions);
	}//setUserAccess

	public String getCanSeeSetCount(){
		if (canSeeSet == null) return "0";
		return Integer.toString(canSeeSet.size());
	}//getCanSeeSetCount

	public boolean checkAccess(int access, javax.servlet.http.HttpServletResponse response) throws Exception {
		if (permissions == null || !permissions.isLoggedIn()) {
			// TODO get the current URL
			//Cookie fromCookie = new Cookie("from","contractor_list.jsp");
			//fromCookie.setMaxAge(3600);
			//response.addCookie(fromCookie);
			response.sendRedirect("logout.jsp?msg=Your session has timed out.  Please log back in");
			return false;
		}//if
		boolean hasAccess = false;
		if (FULL == access) {
			if (isAdmin())
				hasAccess = true;
		}//if
		if (CON_EDIT == access) {
			if (isAdmin()
					|| (isContractor()
					&& userID.equals(thisPageID)))
				hasAccess = true;
		}//if
		if (OP_VIEW == access) {
			if (isAdmin()
					|| seesAll
					|| (isContractor() && userID != null && userID.equals(thisPageID))
					|| (null!=canSeeSet && canSeeSet.contains(thisPageID))
					|| (null!=auditorCanSeeSet && auditorCanSeeSet.contains(thisPageID)))
				hasAccess = true;
		}//if
		if (NOT_CON == access) {
			if (isAdmin()
					|| isAuditor()
					|| isOperator()
					|| isCorporate())
				hasAccess = true;
		}//if
		if (LOGGED_IN == access) {
			if (userID.equals(thisPageID))
				hasAccess = true;
		}//if
		if (PQF_VIEW == access) {
			if (!isContractor()
					&& (isAdmin()
					|| seesAll
					|| canSeeSet.contains(thisPageID)
					|| auditorCanSeeSet.contains(thisPageID)))
				hasAccess = true;
		}//if
		if (OP_EDIT == access) {
			if (isAdmin()
					|| ((isOperator() || isCorporate()) &&
						userID != null && userID.equals(thisPageID)))
				hasAccess = true;
		}//if
		if (BASIC == access) {
				hasAccess = true;
		}//if
		if (!hasAccess) {
			response.sendRedirect("logout.jsp?msg=The requested page is not available");
			return false;
		}//if
		return true;
	}//checkAccess

	public boolean canSeePage() {
		return true;
	}//canSeePage

	public boolean canEditPage(String editID) {
		if (isAdmin() || userID.equals(editID))
			return true;
		return false;
	}//canEditPage

	public boolean isAdmin() {
		if(permissions == null)
			return false;
		return permissions.hasGroup(GROUP_ADMIN);
		//return ADMIN_TYPE.equals(userType);
	}//isAdmin
	public boolean isAuditor() {
		if(permissions == null)
			return false;
		return permissions.hasGroup(GROUP_AUDITOR);	
		//return AUDITOR_TYPE.equals(userType);
	}//isAuditor

	public boolean isContractor() {
		/*
		if(permissions == null)
			return false;
		return permissions.hasGroup(GROUP_CONTRACTOR);
		*/
		return CONTRACTOR_TYPE.equals(userType);
	}//isContractor
	public boolean isOperator() {
		return OPERATOR_TYPE.equals(userType);
	}//isOperator
	public boolean isCorporate() {
		return CORPORATE_TYPE.equals(userType);
	}//isCorporate

	public boolean canSeeAuditCategory(String catID, String conID) {
		if (isAdmin())
			return true;
		if (isOperator()){
			if (isMainAccount)
				return true;
			return (!Utilities.arrayContains(RESTRICTED_AUDIT_CATEGORIES,catID) || 
					userAccess.hasAccess(OpPerms.ViewFullPQF));
		}//if
		if (isAuditor())
			return (!Utilities.arrayContains(RESTRICTED_AUDIT_CATEGORIES,catID) || auditorPQFSet.contains(conID));
		if (isContractor())
// incomplete
			return true;
		return true;
	}//canSeeAuditCategory

	public void setAuditorPermissions(String id) throws Exception {
		auditorOfficeSet = new HashSet<String>();
		String seelctQuery = "SELECT id FROM contractor_info WHERE auditor_id="+id+";";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(seelctQuery);
			while (SQLResult.next())
				auditorOfficeSet.add(SQLResult.getString("id"));
			SQLResult.close();
			auditorDesktopSet = new HashSet<String>();
			seelctQuery = "SELECT id FROM contractor_info WHERE desktopAuditor_id="+id+";";
			SQLResult = SQLStatement.executeQuery(seelctQuery);
			while (SQLResult.next())
				auditorDesktopSet.add(SQLResult.getString("id"));
			SQLResult.close();
			auditorDesktopSet.addAll(auditorOfficeSet);

			auditorDaSet = new HashSet<String>();
			seelctQuery = "SELECT id FROM contractor_info WHERE daAuditor_id="+id+";";
			SQLResult = SQLStatement.executeQuery(seelctQuery);
			while (SQLResult.next())
				auditorDaSet.add(SQLResult.getString("id"));
			SQLResult.close();

			auditorPQFSet = new HashSet<String>();
			seelctQuery = "SELECT id FROM contractor_info WHERE pqfAuditor_id="+id+";";
			SQLResult = SQLStatement.executeQuery(seelctQuery);
			while (SQLResult.next())
				auditorPQFSet.add(SQLResult.getString("id"));
			SQLResult.close();
			auditorPQFSet.addAll(auditorDesktopSet);
		}finally{
			DBClose();
		}//finally		
	}//setAuditorPermissions
	
	public boolean canVerifyAudit(String auditType, String conID) {
		if (isAdmin() ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) && auditorPQFSet.contains(conID)) ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && auditorDesktopSet.contains(conID)) ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) && auditorDaSet.contains(conID)) ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType) && auditorOfficeSet.contains(conID)))
			return true;
		return false;
	}//canVerifyAudit

	public String getWhoIsDetail(){
		if (isAdmin() || isAuditor())
			return userName+",PICS";			
		return uBean.name+","+userName+","+userType;
	}//getWhoIsDetail
	/*
	public void setOperatorPermissions(String id) throws Exception {
		UserBean uBean = new UserBean();
		
		String Query = "SELECT * FROM userscontractor_info WHERE auditor_id="+id+";";
		ResultSet SQLResult = SQLStatement.executeQuery(Query);
		while (SQLResult.next())
			auditorOfficeSet.add(SQLResult.getString("id"));
		SQLResult.close();

		auditorDesktopSet = new HashSet();
		Query = "SELECT * FROM contractor_info WHERE desktopAuditor_id="+id+";";
		SQLResult = SQLStatement.executeQuery(Query);
		while (SQLResult.next())
			auditorDesktopSet.add(SQLResult.getString("id"));
		SQLResult.close();

		auditorPQFSet = new HashSet();
		Query = "SELECT * FROM contractor_info WHERE pqfAuditor_id="+id+";";
		SQLResult = SQLStatement.executeQuery(Query);
		while (SQLResult.next())
			auditorPQFSet.add(SQLResult.getString("id"));
		SQLResult.close();
		DBClose();
	}//setOperatorPermissions
*/
	public Permissions getPermissions() {
		return permissions;
	}
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		this.userID = this.permissions.getAccountIdString();
		this.userType = this.permissions.getAccountType();
	}
	
}//PermissionsBean