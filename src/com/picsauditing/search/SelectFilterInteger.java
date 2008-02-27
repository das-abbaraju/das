package com.picsauditing.search;

import com.picsauditing.PICS.Utilities;

public class SelectFilterInteger extends SelectFilter {
	
	public SelectFilterInteger(String name, String whereClause, String value) {
		super(name, whereClause, value, "", "");
	}
	public SelectFilterInteger(String name, String whereClause, String value, 
			String defaultValue, String ignoreValue) {
		super(name, whereClause, value, defaultValue, ignoreValue);
	}

	public String getWhere() {
		return getWhereRaw().replace("?", Utilities.intToDB(value));
	}
}
