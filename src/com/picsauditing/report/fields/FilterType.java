package com.picsauditing.report.fields;

public enum FilterType {
	AccountID(DisplayType.Number),
	Autocomplete(DisplayType.String),
	Boolean(DisplayType.Boolean),
	Date(DisplayType.String),
	DateTime(DisplayType.String),
	Float(DisplayType.Number),
	Integer(DisplayType.Number),
	ShortList(DisplayType.String),
	String(DisplayType.String),
	UserID(DisplayType.Number);

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
