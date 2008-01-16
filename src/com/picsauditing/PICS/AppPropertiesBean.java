package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.Properties;

public class AppPropertiesBean extends DataBean {
	private Properties props = new Properties();
	
	/**
	 * Usage:
	 * AppPropertiesBean props = new AppPropertiesBean();
	 * String email_welcome = props.getValue("email_welcome");
	 */
	public String get(String property) throws Exception {
		if (props.size() == 0) this.getValues();
		
		String value = props.get(property).toString();
		
		if (value == null) return "";
		
		return value;
	}

	private void getValues() throws Exception {
		String sql = "SELECT property, value FROM app_properties";
		this.props.clear();
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			while (SQLResult.next())
				props.put(SQLResult.getString("property"), SQLResult.getString("value"));
			SQLResult.close();
		}finally{
			DBClose();
		}
	}
	
	public String replace(String body, String parameter) throws Exception {
		if (props.size() == 0) this.getValues();
		String value = this.props.get(parameter).toString();
		return this.replace(body, parameter, value);
	}
	
	public String replace(String body, String parameter, String value) throws Exception {
		body.replace("${"+parameter+"}", value);
		return body;
	}
}
