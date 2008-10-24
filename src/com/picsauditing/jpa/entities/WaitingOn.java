package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

/*
 * Contractor Waiting On 
 */
public enum WaitingOn {
	None, Contractor, Pics, Operator;

	static public Map<Integer, WaitingOn> getMap() {
		Map<Integer, WaitingOn> map = new HashMap<Integer, WaitingOn>();
		for (WaitingOn value : WaitingOn.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}

	static public WaitingOn valueOf(int waitingOn) {
		for (WaitingOn value : WaitingOn.values()) {
			if (waitingOn == value.ordinal())
				return value;
		}
		return null;
	}

	@Override
	public String toString() {
		if (this.equals(WaitingOn.None))
			return "";
		return super.toString();
	}
}
