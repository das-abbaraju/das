package com.picsauditing.report.fields;

public enum FilterType {
	AccountID(DisplayType.Integer),
	Autocomplete(DisplayType.String),
	Boolean,
	Date,
	DateTime,
	Float,
	Integer,
	ShortList(DisplayType.String),
	String,
	UserID(DisplayType.Integer);

	private DisplayType displayType;
	
	private FilterType() {
		displayType = DisplayType.valueOf(this.toString());
	}

	private FilterType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}
}
