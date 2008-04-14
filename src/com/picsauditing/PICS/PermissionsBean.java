package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

import org.jboss.util.NullArgumentException;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditType;

/**
 * @deprecated
 * @see Permissions
 */
public class PermissionsBean extends DataBean {
	
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

	public String getCanSeeSetCount() {
		if (canSeeSet == null) return "0";
		return Integer.toString(canSeeSet.size());
	}

	public boolean isAdmin() {
		if (this.permissions == null) return false;
		return this.permissions.isAdmin();
	}
	
	public boolean isAuditor() {
		if (this.permissions == null) return false;
		return this.permissions.isAuditor();
	}
	public boolean isContractor() {
		return permissions.isContractor();
	}
	public boolean isOperator() {
		return permissions.isOperator();
	}
	public boolean isCorporate() {
		return permissions.isCorporate();
	}

	public void setAuditorPermissions() throws Exception {
		auditorOfficeSet = new HashSet<String>();
		auditorDesktopSet = new HashSet<String>();
		auditorDaSet = new HashSet<String>();
		auditorPQFSet = new HashSet<String>();
		
		auditorCanSeeSet = new HashSet<String>();
		try{
			String sql = "SELECT auditTypeID, conID FROM contractor_audit " +
				"WHERE auditorID = "+getPermissions().getUserId();
			
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			while (SQLResult.next()) {
				String conID = SQLResult.getString("conID");
				int auditTypeID = SQLResult.getInt("auditTypeID");
				auditorCanSeeSet.add(conID);
				if (AuditType.PQF == auditTypeID || AuditType.DESKTOP == auditTypeID || AuditType.OFFICE == auditTypeID) {
					auditorPQFSet.add(conID);
				}
				if (AuditType.DESKTOP == auditTypeID) {
					auditorDesktopSet.add(conID);
				}
				if (AuditType.OFFICE == auditTypeID) {
					auditorOfficeSet.add(conID);
				}
				if (AuditType.DA == auditTypeID) {
					auditorDaSet.add(conID);
				}
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