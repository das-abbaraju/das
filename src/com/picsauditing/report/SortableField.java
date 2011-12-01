package com.picsauditing.report;

public class SortableField {
	public String field;
	public boolean ascending = true;

	public String toString() {
		if (ascending)
			return field;
		return field + " DESC";
	}
}
