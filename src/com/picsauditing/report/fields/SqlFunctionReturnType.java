package com.picsauditing.report.fields;

// todo: implement all the missing ones
public enum SqlFunctionReturnType {
	Year(FilterType.Number, DisplayType.Number),
	Integer(FilterType.Number, DisplayType.Number);

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
