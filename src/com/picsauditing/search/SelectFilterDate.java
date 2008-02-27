package com.picsauditing.search;

import com.picsauditing.PICS.DateBean;

public class SelectFilterDate extends SelectFilter {

	public SelectFilterDate(String name, String whereClause, String value) {
		super(name, whereClause, value, "", "");
	}
	public SelectFilterDate(String name, String whereClause, String value, 
			String defaultValue, String ignoreValue) {
		super(name, whereClause, value, defaultValue, ignoreValue);
	}

	public String getWhere() {
		try {
			return getWhereRaw().replace("?", DateBean.toDBFormat(value));
		} catch (Exception e) {
			return "";
		}
	}
}
