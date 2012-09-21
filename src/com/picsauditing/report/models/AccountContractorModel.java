package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.ReportJoin;

public class AccountContractorModel extends AbstractModel {
	
	public AccountContractorModel(Permissions permissions) {
		fromTable = new ContractorTable("contractor");
		availableFields = fromTable.getAvailableFields(permissions);
		
		ReportJoin accountJoin = fromTable.getJoin("account");
		addJoin(accountJoin, permissions);
		addJoin(accountJoin.getTable().getJoin("accountContact"), permissions);
		addJoin(accountJoin.getTable().getJoin("accountNaics"), permissions);
		
		// TODO adjust these columns
		// hideAccountID();
		// rootTable.removeField("accountName");
		// rootTable.removeField("accountType");
	}
}
