package com.picsauditing.PICS;

import java.sql.ResultSet;

public class AppPropertiesBean extends DataBean {
	
	public AppPropertiesBean() {
		
	}
	/**
	 * Usage:
	 * AppPropertiesBean props = new AppPropertiesBean();
	 * String email_welcome = props.getValue("email_welcome");
	 */
	public String get(String property) throws Exception {
		String sql = "SELECT value FROM app_properties WHERE property = '"+property+"'";
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
}
