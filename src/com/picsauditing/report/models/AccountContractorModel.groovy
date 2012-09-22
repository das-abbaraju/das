package com.picsauditing.report.models;

import java.util.Map;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.ContractorTable
import com.picsauditing.report.tables.ReportForeignKey
import com.picsauditing.report.tables.ReportOnClause
import com.picsauditing.report.tables.ReportTable
import com.picsauditing.util.Strings;

public class AccountContractorModel extends AbstractModel {
	static Map joinSpec = [
		alias: "Contractor",
		joins: [
			[
				key: ContractorTable.Account,
				alias: "Account",
				joins: [
					[
						key: "Contact"
					],[
						key: "Naics"
					]
				]
			],[
				key: ContractorTable.PQF
			],[
				key: ContractorTable.Flag
			],[
				key: ContractorTable.CustomerService
			]
		]
	]
	
	public Map getJoinSpec() {
		return joinSpec;
	}

	public AccountContractorModel(Permissions permissions) {
		super(permissions, new ContractorTable())
		
		// TODO adjust these columns
		// hideAccountID();
		// rootTable.removeField("accountName");
		// rootTable.removeField("accountType");
	}

}
