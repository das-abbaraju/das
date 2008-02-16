package com.picsauditing.PICS;

import java.sql.ResultSet;

public class AppPropertiesBean extends DataBean {
	
	/**
	 * Usage:
	 * AppPropertiesBean props = new AppPropertiesBean();
	 * String email_welcome = props.getValue("email_welcome");
	 */
	public String get(String property) throws Exception {
		String sql = "SELECT value FROM app_properties WHERE property = '"+eqDB(property)+"'";
		String value = "";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			if(SQLResult.next()) {
				value = SQLResult.getString("value");
			}
			SQLResult.close();
		}finally{
			DBClose();
		}
		return value;
	}
	
	public String save(String property, String value) throws Exception {
		String sql = "REPLACE app_properties SET property = '"+eqDB(property)+"', value = '"+eqDB(value)+"'";
		try {
			DBReady();
			SQLStatement.executeUpdate(sql);
		}finally{
			DBClose();
		}
		return value;
	}
}
