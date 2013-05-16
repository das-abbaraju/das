package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

public class UserModel extends AbstractModel {

	public static final String ACCOUNT_TYPE = "AccountType";

	public UserModel(Permissions permissions) {
		super(permissions, new UserTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "User");
		spec.category = FieldCategory.AccountInformation;

		ModelSpec account = spec.join(UserTable.Account);
		account.alias = "Account";

		if (permissions.isOperatorCorporate() || permissions.isPicsEmployee()) {
			ModelSpec operator = account.join(AccountTable.Operator);
			operator.category = FieldCategory.AccountInformation;
			operator.minimumImportance = FieldImportance.Required;
			operator.alias = "Operator";

			ModelSpec reporting = operator.join(OperatorTable.Reporting);
			reporting.category = FieldCategory.ReportingClientSite;
			reporting.minimumImportance = FieldImportance.Required;
			reporting.alias = "ReportingClient";
		}

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("UsersManage.action?account={AccountID}");

		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?account={AccountID}&user={UserID}");

		Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setCategory(FieldCategory.AccountInformation);
		accountType.setTranslationPrefixAndSuffix("AccountType", "");
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

		return fields;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		String userWhere = "";

		if(permissions.isOperator()) {
			userWhere = "User.accountID = " + permissions.getAccountIdString();
		}
		if(permissions.isCorporate()) {
			userWhere = "User.accountID IN (" + Strings.implode(permissions.getVisibleAccounts())+")";
		}

		return userWhere;
	}
}