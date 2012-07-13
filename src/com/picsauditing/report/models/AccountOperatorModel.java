package com.picsauditing.report.models;

import com.picsauditing.report.tables.OperatorTable;

public class AccountOperatorModel extends AccountModel {
	public AccountOperatorModel() {
		super();
		rootTable.removeField("accountName");

		OperatorTable operatorTable = new OperatorTable();
		rootTable.addAllFieldsAndJoins(operatorTable);
		
		parentTable = operatorTable;
	}

}
