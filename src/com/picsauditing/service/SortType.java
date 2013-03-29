package com.picsauditing.service;

public enum SortType {

	ALPHA_SORT, DATE_ADDED_SORT, LAST_VIEWED_SORT;

	@Override
	public String toString() {
		if (this == ALPHA_SORT) {
			return "alpha";
		} else if (this == DATE_ADDED_SORT) {
			return "dateAdded";
		} else if (this == LAST_VIEWED_SORT) {
			return "lastViewed";
		}

		throw new IllegalStateException("Missing String value for " + this.name());
	}

}
