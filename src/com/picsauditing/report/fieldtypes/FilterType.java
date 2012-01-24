package com.picsauditing.report.fieldtypes;

public enum FilterType {
	String(ExtFieldType.String), Number, Boolean(ExtFieldType.Boolean), Date(ExtFieldType.Date), AccountName(
			ExtFieldType.String), AccountStatus(ExtFieldType.String), AccountType(ExtFieldType.String), LowMedHigh(
			ExtFieldType.String), AuditStatus(ExtFieldType.String), Trades, @Deprecated
	Enum, @Deprecated
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

	private FilterType(ExtFieldType type) {
		fieldType = type;
	}

	public String getSortType() {
		return sortType;
	}

	public ExtFieldType getFieldType() {
		return fieldType;
	}
}
