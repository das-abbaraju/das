package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

public enum MultiYearScope {
	LastYearOnly("Last Year Only"),
	TwoYearsAgo("Two Years Ago"),
	ThreeYearsAgo("Three Years Ago"), 
	ThreeYearAverage("Three Year Average"), 
	ThreeYearWeightedAverage("Three Year Weighted Average"), 
	AllThreeYears("All Three Years");
	
	private String description;
	private MultiYearScope(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	static public Map<Integer, MultiYearScope> getMap() {
		Map<Integer, MultiYearScope> map = new HashMap<Integer, MultiYearScope>();
		for (MultiYearScope value : MultiYearScope.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}
}
