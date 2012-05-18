package com.picsauditing.report.fields;

public enum FilterType {
	String(ExtFieldType.String), Integer(ExtFieldType.Int), Float(ExtFieldType.Float), Boolean(ExtFieldType.Boolean), Date(ExtFieldType.Date), DateTime(
			ExtFieldType.Date), AccountName(ExtFieldType.String), AccountStatus, AccountType, LowMedHigh(
			ExtFieldType.String), AccountLevel, AuditStatus, Enum, Autocomplete, List;

	/**
	 * We might want to add sortTypes later on to support alternative sorting
	 * 
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.SortTypes
	 */
	private int reportID = 0;
	private String sortType = null;
	/**
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Types
	 * @see http://docs.sencha.com/ext-js/4-0/#!/api/Ext.data.Field
	 */
	private ExtFieldType fieldType = ExtFieldType.Auto;

	private FilterType() {

	}

	private FilterType(ExtFieldType type) {
		fieldType = type;
	}
	
	private FilterType(int reportID) {
		this.reportID = reportID;
	}

	public String getSortType() {
		return sortType;
	}

	public ExtFieldType getFieldType() {
		return fieldType;
	}
	
	public boolean isList() {
		return this == List;
	}

	public int getReportID() {
		return reportID;
	}
}
