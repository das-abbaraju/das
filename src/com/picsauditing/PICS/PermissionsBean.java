package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;

import org.jboss.util.NullArgumentException;

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
	public OperatorBean oBean = null;
	public ArrayList<String> allFacilitiesAL = null;
	private Permissions permissions = null;

	// TODO: remove this method after we get rid of the FormBean
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

	public boolean isAdmin() {
		if (this.permissions == null) return false;
		return this.permissions.isAdmin();
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