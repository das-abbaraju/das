package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldCategory;

public class AccountOperatorModel extends AbstractModel {

	public AccountOperatorModel(Permissions permissions) {
		super(permissions, new AccountTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Account");
		spec.join(AccountTable.Operator).category = FieldCategory.ReportingClientSite;
		spec.join(AccountTable.Naics);
		return spec;
	}
}
