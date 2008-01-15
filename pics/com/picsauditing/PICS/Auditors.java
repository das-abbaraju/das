package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;

public class Auditors extends DataBean{
	ArrayList<String> auditorsAL = null;
	
	public void resetAuditorsAL() throws Exception{
		auditorsAL = null;
	}//resetAuditorsAL

	public void setAuditorsALFromDB() throws Exception{
		if (null!=auditorsAL)
			return;
		auditorsAL = new ArrayList<String>();
		// USERMOVE
		//String selectQuery = "SELECT id, name FROM accounts WHERE type = 'Auditor' AND active = 'Y' ORDER BY name ASC";
		String selectQuery = "SELECT id, name FROM users WHERE isGroup = 'No' AND isActive = 'Yes' AND id IN (SELECT userID FROM usergroup WHERE groupID = '11') ORDER BY name ASC";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()){
				auditorsAL.add(SQLResult.getString("id"));
				auditorsAL.add(SQLResult.getString("name"));
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}//finally
		return;
	}//setALFromDB

	public String getNameFromID(String auditorID) throws Exception{
		if (auditorID.length() == 0) return "";
		setAuditorsALFromDB();
		for (ListIterator li = auditorsAL.listIterator();li.hasNext();){
			String tempID = (String)li.next();
			if (tempID.equals(auditorID))
				return (String)li.next();
			li.next();
		}//for
		//throw new Exception("Failed to find auditor"+auditorID);
		return "";
	}//getNameFromID

	public String getAuditorsSelect(String name, String classType, String selectedAuditorID) throws Exception {
		setAuditorsALFromDB();
		return Utilities.inputSelect2First(name,classType,selectedAuditorID,(String[])auditorsAL.toArray(new String[0]),
			SearchBean.DEFAULT_AUDITOR_ID,SearchBean.DEFAULT_AUDITOR);
	}//getAuditorsSelect
}//Auditors