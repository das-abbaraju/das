package com.picsauditing.report.fields;

public enum FilterType {
	AccountID(DisplayType.RightAlign),
	Autocomplete(DisplayType.LeftAlign),
	Boolean(DisplayType.CheckMark),
	Date(DisplayType.LeftAlign),
	DateTime(DisplayType.LeftAlign),
	Float(DisplayType.RightAlign),
	Integer(DisplayType.RightAlign),
	ShortList(DisplayType.LeftAlign),
	String(DisplayType.LeftAlign),
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
