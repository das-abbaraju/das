package com.picsauditing.search;

public class SelectFilterInteger extends SelectFilter {

	public SelectFilterInteger(String name, String whereClause, Integer value) {
		super(name, whereClause, value.toString(), "0", "0");
	}

	public SelectFilterInteger(String name, String whereClause, String value, String defaultValue, String ignoreValue) {
		super(name, whereClause, value, defaultValue, ignoreValue);
	}

	public String getWhere() {

		try {
			Integer id = Integer.parseInt(value);
			return getWhereRaw().replace("?", id.toString());
		} catch (Exception ex) {
			return "";
		}
	}
}
