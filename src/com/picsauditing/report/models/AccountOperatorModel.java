package com.picsauditing.report.models;


import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldCategory;

public class AccountOperatorModel extends AbstractModel {

	public AccountOperatorModel(Permissions permissions) {
		super(permissions, new AccountTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Account");
		ModelSpec operator = spec.join(AccountTable.Operator);
		operator.category = FieldCategory.ReportingClientSite;
		operator.alias = "Operator";
		spec.join(AccountTable.Naics);
		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("FacilitiesEdit.action?operator={AccountID}");
		return fields;
	}
}
