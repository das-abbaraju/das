package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldCategory;

public class AccountModel extends AbstractModel {
	public AccountModel(Permissions permissions) {
		super(permissions, new AccountTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec account = new ModelSpec(null, "Account");
		account.join(AccountTable.Contact).category = FieldCategory.ContactInformation;
		account.join(AccountTable.Naics);
		return account;
	}
}
