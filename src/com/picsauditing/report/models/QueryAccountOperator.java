package com.picsauditing.report.models;

import com.picsauditing.report.tables.Operator;

public class QueryAccountOperator extends QueryAccount {
	public QueryAccountOperator() {
		super();
		primaryTable.removeField("accountName");

		Operator operator = new Operator();
		primaryTable.addAllFieldsAndJoins(operator);
	}

}
