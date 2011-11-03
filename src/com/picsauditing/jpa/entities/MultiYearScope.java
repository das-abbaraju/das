package com.picsauditing.jpa.entities;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public enum MultiYearScope {
	LastYearOnly("Last Year Only"),
	TwoYearsAgo("Two Years Ago"),
	ThreeYearsAgo("Three Years Ago"),
	ThreeYearAverage("Three Year Average");

	@Deprecated
	private String description;

	private MultiYearScope(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public String getAuditFor() {
		if (this == ThreeYearAverage)
			return "Average";

		Calendar cal = Calendar.getInstance();
		if (this == ThreeYearsAgo)
			cal.add(Calendar.YEAR, -3);
		if (this == TwoYearsAgo)
			cal.add(Calendar.YEAR, -2);
		if (this == LastYearOnly)
			cal.add(Calendar.YEAR, -1);
		return "" + cal.get(Calendar.YEAR);
	}

	public static MultiYearScope getScopeFromYear(int year) {
		Calendar cal = Calendar.getInstance();

		if ((cal.get(Calendar.YEAR) - 3) == year)
			return ThreeYearsAgo;
		if ((cal.get(Calendar.YEAR) - 2) == year)
			return TwoYearsAgo;
		if ((cal.get(Calendar.YEAR) - 1) == year)
			return LastYearOnly;

		return null;
	}

	static public Map<Integer, MultiYearScope> getMap() {
		Map<Integer, MultiYearScope> map = new HashMap<Integer, MultiYearScope>();
		for (MultiYearScope value : MultiYearScope.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}
}
