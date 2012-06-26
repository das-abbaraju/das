package com.picsauditing.report.models;

import com.picsauditing.report.tables.OperatorTable;

public class AccountOperatorModel extends AccountModel {
	public AccountOperatorModel() {
		super();
		primaryTable.removeField("accountName");

		OperatorTable operatorTable = new OperatorTable();
		primaryTable.addAllFieldsAndJoins(operatorTable);
	}

}
