package com.picsauditing.access;

import java.sql.*;


public class UserList extends com.picsauditing.PICS.DataBean {
	public String accountID = "";
	public UserDO userDO = null;

	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;

	public void setList(String accountID) throws Exception {
		this.accountID = accountID;
		String selectQuery = "SELECT * FROM users WHERE accountID="+
				accountID+" ORDER BY name;";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			userDO = new UserDO();
		} catch (Exception e) {
			closeList();
			throw e;
		}//catch		
	}//setList

	public boolean isNext() throws Exception{
		if (!listRS.next())
			return false;
		count++;
		userDO.setFromResultSet(listRS);
		return true;
	}//isNextRecord

	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		if (null != listRS) {
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}//closeList
}//UserList