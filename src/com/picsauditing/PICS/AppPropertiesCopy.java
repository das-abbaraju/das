package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.HashMap;

import com.mysql.jdbc.Connection;

public class AppPropertiesCopy extends DataBean {
	private HashMap<String, String> props = new HashMap<String, String>();
	private HashMap<String, String> tokens = new HashMap<String, String>();
	
	/**
	 * Usage:
	 * AppPropertiesBean props = new AppPropertiesBean();
	 * String email_welcome = props.getValue("email_welcome");
	 */
	public String get(String property) throws Exception {
		if (props.size() == 0) this.getValues();
		
		String value = props.get(property);
		
		value = replaceAll(value, tokens);
		value = replaceAll(value, props);
		value = replaceAll(value, tokens);
		value = replaceAll(value, props);
		
		if (value.contains("${")) System.out.println("Could not replace all tokens from: "+value);
		
		return value;
	}
	
	private void getValues() throws Exception {
		String sql = "SELECT property, value FROM app_properties";
		this.props.clear();
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql);
			while (SQLResult.next()) {
				props.put(SQLResult.getString("property"), SQLResult.getString("value"));
			}
			SQLResult.close();
		}finally{
			DBClose();
		}
	}
	
	private String replaceAll(String value, HashMap<String, String> tokens) {
		if (!value.contains("${")) return value;
		for(String token : tokens.keySet()) {
			value = value.replace("${"+token+"}", tokens.get(token));
		}
		return value;
	}

	private String replace(String body, String parameter) throws Exception {
		if (props.size() == 0) this.getValues();
		String value = this.props.get(parameter).toString();
		return this.replace(body, parameter, value);
	}
	
	private String replace(String body, String parameter, String value) throws Exception {
		body = body.replace("${"+parameter+"}", value);
		return body;
	}
	
	public void addToken(String key, String value) {
		tokens.put(key, value);
	}

	public void setConnection(Connection conn) {
		this.Conn = conn;
	}

	public HashMap<String, String> getTokens() {
		return tokens;
	}

	public void setTokens(HashMap<String, String> tokens) {
		this.tokens = tokens;
	}
}
