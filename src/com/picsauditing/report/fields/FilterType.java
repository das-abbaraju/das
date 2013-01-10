package com.picsauditing.report.fields;

public enum FilterType {
	AccountID(DisplayType.RightAlign),
	Autocomplete(DisplayType.String),
	Boolean,
	Date,
	DateTime,
	Float(DisplayType.RightAlign),
	Integer(DisplayType.RightAlign),
	ShortList(DisplayType.String),
	String,
	UserID(DisplayType.RightAlign);

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
