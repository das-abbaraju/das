package com.picsauditing.report.models;

import com.picsauditing.report.tables.Operator;

public class AccountOperatorModel extends AccountModel {
	public AccountOperatorModel() {
		super();
		primaryTable.removeField("accountName");

		Operator operator = new Operator();
		primaryTable.addAllFieldsAndJoins(operator);
	}

}
