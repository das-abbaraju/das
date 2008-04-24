package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

/*
 * Contractor Risk Level
 */
public enum LowMedHigh {
	None, Low, Med, High;
	
	static public Map<Integer, LowMedHigh> getMap() {
		Map<Integer, LowMedHigh> map = new HashMap<Integer, LowMedHigh>();
		for(LowMedHigh value : LowMedHigh.values()) {
			map.put(value.ordinal(), value);
		}
		return map; 
	}
}
