package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.FieldCategory
import com.picsauditing.report.tables.InvoiceCommissionTable

public class InvoiceCommissionModel extends AbstractModel {
	static def joinSpec = [
		alias: "Account",
		joins: [
			[
				key: "Contact",
				category: FieldCategory.ContactInformation
			],[
				key: "Naics"
			]
		]
	]

	public Map getJoinSpec() {
		return joinSpec;
	}

	public InvoiceCommissionModel(Permissions permissions) {
		super(permissions, new InvoiceCommissionTable())
//		rootTable.getTable("account").includeRequiredAndAverageColumns();
//		rootTable.removeJoin("accountContact");
//		rootTable.getTable("contractor").includeRequiredAndAverageColumns();
//		rootTable.removeJoin("contractorCustomerService");
	}
}