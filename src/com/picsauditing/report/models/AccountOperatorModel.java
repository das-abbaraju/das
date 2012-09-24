package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.AccountTable
import com.picsauditing.report.tables.FieldCategory

public class AccountOperatorModel extends AbstractModel {
	static def joinSpec = [
		alias: "Account",
		joins: [
			[
				key: "Operator",
				alias: "Operator",
				category: FieldCategory.ContactInformation
			],[
				key: "Naics"
			]
		]
	]

	public Map getJoinSpec() {
		return joinSpec;
	}

	public AccountOperatorModel(Permissions permissions) {
		super(permissions, new AccountTable())
	}
}
