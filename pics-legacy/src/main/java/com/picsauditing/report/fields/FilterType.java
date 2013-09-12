package com.picsauditing.report.fields;


public enum FilterType {

	AccountID(QueryFilterOperator.Equals),
	Autocomplete(QueryFilterOperator.In),
	Boolean(QueryFilterOperator.Equals),
	Date(QueryFilterOperator.LessThan),
	Multiselect(QueryFilterOperator.In),
	Number(QueryFilterOperator.Equals),
	String(QueryFilterOperator.Contains),
	UserID(QueryFilterOperator.Equals);

	public QueryFilterOperator defaultOperator;

	FilterType(QueryFilterOperator defaultOperator) {
		this.defaultOperator = defaultOperator;
	}
}
