package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

public enum MultiYearScope {
	LastYearOnly, ThreeYearAverage, AllThreeYears;

	static public Map<Integer, MultiYearScope> getMap() {
		Map<Integer, MultiYearScope> map = new HashMap<Integer, MultiYearScope>();
		for (MultiYearScope value : MultiYearScope.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}
}
