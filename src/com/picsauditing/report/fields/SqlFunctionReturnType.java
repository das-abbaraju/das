package com.picsauditing.report.fields;

// todo: implement all the missing ones
public enum SqlFunctionReturnType {
	Year(FilterType.Integer, DisplayType.Number),
	Integer(FilterType.Integer, DisplayType.Number);

	private FilterType filterType;
	private DisplayType displayType;

	SqlFunctionReturnType(FilterType filterType, DisplayType displayType) {
		this.filterType = filterType;
		this.displayType = displayType;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}

	public FilterType getFilterType() {
		return filterType;
	}
}
