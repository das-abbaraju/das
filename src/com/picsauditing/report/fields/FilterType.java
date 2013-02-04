package com.picsauditing.report.fields;

public enum FilterType {
	AccountID(DisplayType.Number),
	Autocomplete(DisplayType.String),
	Boolean(DisplayType.Boolean),
	Date(DisplayType.String),
	DateTime(DisplayType.String),
	Float(DisplayType.Number),
	Integer(DisplayType.Number),
	Multiselect(DisplayType.String),
	Number(DisplayType.Number),
	ShortList(DisplayType.String),
	String(DisplayType.String),
	UserID(DisplayType.Number);

	// todo: verify that displayType is NOT a property of a FilterType
	private DisplayType displayType;

	private FilterType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}
}
