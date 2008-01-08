package com.picsauditing.access;

import java.sql.ResultSet;

// This may be now duplicated in Permission.java

public class AccessDO extends com.picsauditing.PICS.DataBean{
	String userID = "";
	String accessType = "";
	String grantedByID = "";

	public AccessDO(){
	}//Constructor

	public void writeToDB()throws Exception{
		try{
			DBReady();
			String insertQuery = "INSERT INTO access (userID,accessType,"+
				"grantedByID,lastUpdate) VALUES("+
				userID+","+accessType+","+grantedByID+",NOW());";
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
	}//writeToDB

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		userID = SQLResult.getString("userID");
		accessType = SQLResult.getString("accessType");
		grantedByID = SQLResult.getString("grantedByID");
	}//setFromResultSet
}//Note
