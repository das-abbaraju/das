package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

import org.jboss.util.NullArgumentException;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;

/**
 * @deprecated
 * @see Permissions
 */
public class PermissionsBean extends DataBean {
/*	History
	8/29/05 jj - created to control access to pages according to account type (admin,auditor,contractor,operator) and restrict privileges (write, read, none)
*/
	private static final String ADMIN_TYPE = "Admin";
	private static final String CONTRACTOR_TYPE = "Contractor";
	private static final String CORPORATE_TYPE = "Corporate";
	private static final String OPERATOR_TYPE = "Operator";
	private static final String[] USER_TYPES = {ADMIN_TYPE,CONTRACTOR_TYPE,CORPORATE_TYPE,OPERATOR_TYPE};

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
	public ArrayList<String> allFacilitiesAL = null;
	private Permissions permissions = null;

	public void setAllFacilitiesFromDB(String conID) throws Exception {
		if (conID == null || conID.equals(""))
			throw new NullArgumentException("oonID must be set when calling setAllFacilitiesFromDB");
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
		} finally {
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

	public String getCanSeeSetCount(){
		if (canSeeSet == null) return "0";
		return Integer.toString(canSeeSet.size());
	}//getCanSeeSetCount

	/**
	 * @deprecated
	 * @return
	 */
	public boolean isAdmin() {
		if (this.permissions == null) return false;
		return this.permissions.isAdmin();
	}
	/**
	 * @deprecated
	 * @return
	 */
	public boolean isAuditor() {
		if (this.permissions == null) return false;
		return this.permissions.isAuditor();
	}
	public boolean isContractor() {
		return CONTRACTOR_TYPE.equals(userType);
	}
	public boolean isOperator() {
		return OPERATOR_TYPE.equals(userType);
	}
	public boolean isCorporate() {
		return CORPORATE_TYPE.equals(userType);
	}

	public void setAuditorPermissions() throws Exception {
		auditorOfficeSet = new HashSet<String>();
		auditorDesktopSet = new HashSet<String>();
		auditorDaSet = new HashSet<String>();
		auditorPQFSet = new HashSet<String>();
		
		auditorCanSeeSet = new HashSet<String>();
		try{
			String sql;
			DBReady();
			
			sql = "SELECT id FROM contractor_info WHERE auditor_id="+this.uBean.id+";";
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			while (SQLResult.next()) {
				auditorOfficeSet.add(SQLResult.getString("id"));
				auditorDesktopSet.add(SQLResult.getString("id"));
				auditorPQFSet.add(SQLResult.getString("id"));
				auditorCanSeeSet.add(SQLResult.getString("id"));
			}
			SQLResult.close();
			
			sql = "SELECT id FROM contractor_info WHERE desktopAuditor_id="+this.uBean.id+";";
			SQLResult = SQLStatement.executeQuery(sql);
			while (SQLResult.next()) {
				auditorDesktopSet.add(SQLResult.getString("id"));
				auditorPQFSet.add(SQLResult.getString("id"));
				auditorCanSeeSet.add(SQLResult.getString("id"));
			}
			SQLResult.close();

			sql = "SELECT id FROM contractor_info WHERE daAuditor_id="+this.uBean.id+";";
			SQLResult = SQLStatement.executeQuery(sql);
			while (SQLResult.next()) {
				auditorDaSet.add(SQLResult.getString("id"));
				auditorCanSeeSet.add(SQLResult.getString("id"));
			}
			SQLResult.close();

			sql = "SELECT id FROM contractor_info WHERE pqfAuditor_id="+this.uBean.id+";";
			SQLResult = SQLStatement.executeQuery(sql);
			while (SQLResult.next()) {
				auditorPQFSet.add(SQLResult.getString("id"));
				auditorCanSeeSet.add(SQLResult.getString("id"));
			}
			SQLResult.close();
			
		} finally {
			DBClose();
		}
	}
	
	public boolean canVerifyAudit(String auditType, String conID) {
		if (isAdmin() ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.PQF_TYPE.equals(auditType) && auditorPQFSet.contains(conID)) ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE.equals(auditType) && auditorDesktopSet.contains(conID)) ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.DA_TYPE.equals(auditType) && auditorDaSet.contains(conID)) ||
			isAuditor() && (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE.equals(auditType) && auditorOfficeSet.contains(conID)))
			return true;
		return false;
	}

	public String getWhoIsDetail(){
		if (isAdmin() || isAuditor())
			return userName+",PICS";			
		return uBean.name+","+userName+","+userType;
	}//getWhoIsDetail
	
	public Permissions getPermissions() {
		if (this.permissions == null) permissions = new Permissions();
		return permissions;
	}
	public void setPermissions(Permissions permissions) {
		this.permissions = permissions;
		this.userID = this.permissions.getAccountIdString();
		this.userType = this.permissions.getAccountType();
	}
}