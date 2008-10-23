package com.picsauditing.jpa.entities;

import java.util.HashMap;
import java.util.Map;

public enum NoteStatus {
	Hidden, Open, Closed;

	static public Map<Integer, NoteStatus> getMap() {
		Map<Integer, NoteStatus> map = new HashMap<Integer, NoteStatus>();
		for (NoteStatus value : NoteStatus.values()) {
			map.put(value.ordinal(), value);
		}
		return map;
	}
}
