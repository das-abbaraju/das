package com.picsauditing.report.fields;

public enum FilterType {
	String(ExtFieldType.String),
	Integer(ExtFieldType.Int),
	Float(ExtFieldType.Float),
	Boolean(ExtFieldType.Boolean),
	Date(ExtFieldType.Date),
	DateTime(ExtFieldType.Date),
	AccountName(ExtFieldType.String),
	AccountID(ExtFieldType.Int),
	UserID(ExtFieldType.Int),
	DaysAgo(ExtFieldType.Int),
	LowMedHigh,
	Enum,
	Autocomplete,
	List;

	/**
	 * We might want to add sortTypes later on to support alternative sorting
	 *
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.SortTypes
	 */
	private String sortType = null;

	/**
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Types
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Field
	 */
	private ExtFieldType fieldType = ExtFieldType.Auto;

	private FilterType() {
	}

	private FilterType(ExtFieldType fieldType) {
		this.fieldType = fieldType;
	}

	public String getSortType() {
		return sortType;
	}

	public ExtFieldType getFieldType() {
		return fieldType;
	}

	public boolean isEnum() {
		return this == Enum;
	}

	public boolean isAutocomplete() {
		return this == Autocomplete;
	}

	public boolean isLowMedHigh() {
		return this == LowMedHigh;
	}
}
