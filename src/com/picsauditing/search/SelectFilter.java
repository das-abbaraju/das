package com.picsauditing.search;

public class SelectFilter {
	private String name;
	private String whereClause;
	private String startingValue;
	private String defaultValue;
	private String value;
	
	public SelectFilter(String name) {
		this.name = name;
	}
	public SelectFilter(String name, String whereClause, String startingValue,
			String defaultValue) {
		this.name = name;
		this.whereClause = whereClause;
		this.startingValue = startingValue;
		this.defaultValue = defaultValue;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWhereClause() {
		return whereClause;
	}
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	public String getStartingValue() {
		return startingValue;
	}
	public void setStartingValue(String startingValue) {
		this.startingValue = startingValue;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
