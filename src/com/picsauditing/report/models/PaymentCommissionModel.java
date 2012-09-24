package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.FieldCategory
import com.picsauditing.report.tables.PaymentCommissionTable

public class PaymentCommissionModel extends AbstractModel {
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

	public PaymentCommissionModel(Permissions permissions) {
		super(permissions, new PaymentCommissionTable())
	}
}