package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

public enum MultiYearScope {
	LastYearOnly("Last Year Only"),
	TwoYearsAgo("Two Years Ago"),
	ThreeYearsAgo("Three Years Ago"),
	ThreeYearAverage("Three Year Average");

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
		if (this == ThreeYearsAgo)
			return "2007";
		if (this == TwoYearsAgo)
			return "2008";
		if (this == LastYearOnly)
			return "2009";

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
