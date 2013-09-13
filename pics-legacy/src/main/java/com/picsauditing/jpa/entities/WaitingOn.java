package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

/*
 * Contractor Waiting On 
 */
public enum WaitingOn implements Translatable {
	None,
	Contractor,
	PICS,
	Operator;

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

	static public WaitingOn fromOrdinal(int waitingOn) {
		return valueOf(waitingOn);
	}

	public static WaitingOn getWorseWaitingOn(WaitingOn waiting1, WaitingOn waiting2) {
		if (waiting2 == null)
			return waiting1;

		if (waiting1 == null) {
			return waiting2;
		}

		if (waiting2.ordinal() > waiting1.ordinal())
			waiting1 = waiting2;
		return waiting1;
	}

	public boolean isNone() {
		return this.equals(None);
	}

	@Transient
	@Override
	public String getI18nKey() {
		return getClass().getSimpleName() + "." + toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
