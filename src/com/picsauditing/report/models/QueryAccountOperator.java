package com.picsauditing.report.models;

import com.picsauditing.report.tables.Operator;

public class QueryAccountOperator extends QueryAccount {
	public QueryAccountOperator() {
		super();
		from.getFields().remove("accountName");

		Operator operator = new Operator();
		from.addJoin(operator);
		operator.addFields();
		operator.addJoins();
	}

}
