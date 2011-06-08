package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

/*
 * Contractor Risk Level
 */
public enum LowMedHigh implements Translatable {
	None, Low, Med, High;

	static public Map<Integer, LowMedHigh> getMap() {
		Map<Integer, LowMedHigh> map = new HashMap<Integer, LowMedHigh>();
		for (LowMedHigh value : LowMedHigh.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}

	static public String getName(int id) {
		for (LowMedHigh value : LowMedHigh.values()) {
			if (value.ordinal() == id)
				return value.toString();
		}
		return "";
	}

	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.toString();
	}

	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
