package com.picsauditing.report.models;

import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.ContractorTable;

public class AccountContractorModel extends AccountModel {
	public AccountContractorModel() {
		super();
		hideAccountID();
		rootTable.removeField("accountName");
		rootTable.removeField("accountType");

		ContractorTable contractorTable = new ContractorTable(rootTable.getPrefix(), rootTable.getAlias());
		contractorTable.includeAllColumns();
		rootTable.addAllFieldsAndJoins(contractorTable);
		parentTable = contractorTable;
	}

	private void hideAccountID() {
		Field accountID = rootTable.getAvailableFields().get("accountID");
		if (accountID != null) {
			accountID.setVisible(false);
		}
	}
}
