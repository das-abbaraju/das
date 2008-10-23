package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

/*
 * Contractor work Status
 */
public enum WorkStatus {
	None, Contractor, Pics, Operator;

	static public Map<Integer, WorkStatus> getMap() {
		Map<Integer, WorkStatus> map = new HashMap<Integer, WorkStatus>();
		for (WorkStatus value : WorkStatus.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}
}
