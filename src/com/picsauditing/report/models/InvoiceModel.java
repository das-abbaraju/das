package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.ContractorTable
import com.picsauditing.report.tables.FieldCategory
import com.picsauditing.report.tables.InvoiceTable

public class InvoiceModel extends AbstractModel {
	static Map joinSpec = [
		alias: "Invoice",
		joins: [
			[
				key: InvoiceTable.Account,
				alias: "Account",
				joins: [
					[
						key: AccountTable.Contact,
						category: FieldCategory.ContactInformation
					],[
						alias: "Contractor",
						key: AccountTable.Contractor
					]
				]
			]
		]
	]
	
	public InvoiceModel(Permissions permissions) {
		super(permissions, new InvoiceTable())
	}

	public Map getJoinSpec() {
		return joinSpec;
	}
}
