package com.picsauditing.report.fields;

public enum FilterType {
	AccountID(DisplayType.Number),
	Autocomplete(DisplayType.String),
	Boolean(DisplayType.Boolean),
	Date(DisplayType.String),
	Multiselect(DisplayType.String),
	Number(DisplayType.Number),
	String(DisplayType.String),
	UserID(DisplayType.Number);

	private DisplayType displayType;

	private FilterType(DisplayType displayType) {
		this.displayType = displayType;
	}

	public DisplayType getDisplayType() {
		return displayType;
	}
}
