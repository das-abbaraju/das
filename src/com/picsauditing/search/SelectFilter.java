package com.picsauditing.search;

import com.picsauditing.util.Strings;

public class SelectFilter {
	private String name;
	private String whereClause;
	private String defaultValue;
	private String ignoreValue;
	protected String value;
	
	public SelectFilter(String name, String whereClause, String value, 
			String defaultValue, String ignoreValue) {
		this.name = name;
		this.whereClause = whereClause;
		this.value = value;
		this.defaultValue = defaultValue;
		this.ignoreValue = ignoreValue;
		if (this.value == null) this.value = this.defaultValue;
	}
	
	public SelectFilter(String name, String whereClause, String value) {
		this(name, whereClause, value, "", "");
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		if (this.ignoreValue != null && this.ignoreValue.equals(value))
			return defaultValue;
		if ("null".equals(value))
			return defaultValue;
		return value;
	}
	
	public String getWhere() {
		return getWhereRaw().replace("?", Strings.escapeQuotes(value));
	}
	
	protected String getWhereRaw() {
		// Create a copy of the whereClause
		if (!isSet())
			return "";
		if (whereClause == null)
			return "";
		
		return whereClause;
	}
	
	public boolean isSet() {
		if (this.value==null)
			return false;
		if (this.ignoreValue != null && this.ignoreValue.equals(value))
			return false;
		return true;
	}
}
