package com.picsauditing.report.models;

import com.picsauditing.report.tables.OperatorTable;

public class AccountOperatorModel extends AccountModel {
	public AccountOperatorModel() {
		super();
		rootTable.removeField("accountID");
		rootTable.removeField("accountName");
		rootTable.removeField("accountType");

		OperatorTable operatorTable = new OperatorTable(rootTable.getPrefix(), rootTable.getAlias());
		rootTable.addAllFieldsAndJoins(operatorTable);

		parentTable = operatorTable;
	}

}
