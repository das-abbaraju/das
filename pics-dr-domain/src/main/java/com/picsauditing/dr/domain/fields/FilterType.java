package com.picsauditing.dr.domain.fields;


import com.picsauditing.dr.domain.fields.QueryFilterOperator;

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
