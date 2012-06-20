package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

public enum NoteStatus implements Translatable {
	Hidden, Open, Closed;

	static public Map<Integer, NoteStatus> getMap() {
		Map<Integer, NoteStatus> map = new HashMap<Integer, NoteStatus>();
		for (NoteStatus value : NoteStatus.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}

	@Transient
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Transient
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
