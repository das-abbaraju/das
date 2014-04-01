package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

public class UsersModel extends AbstractModel {

	public static final String ACCOUNT_TYPE = "AccountType";

	public UsersModel(Permissions permissions) {
		super(permissions, new UserTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "User");

		ModelSpec account = spec.join(UserTable.Account);
		account.alias = "Account";

        ModelSpec accountUser = account.join(AccountTable.AccountManager);
        accountUser.alias = "AccountManager";

        ModelSpec accountManager = accountUser.join(AccountUserTable.User);
        accountManager.alias = "AccountManagerUser";

        ModelSpec operator = account.join(AccountTable.OperatorInfo);
        operator.alias = "Operator";

        ModelSpec reporting = operator.join(OperatorTable.Reporting);
        reporting.alias = "ReportingClient";

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("UsersManage.action?account={AccountID}");

		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?account={AccountID}&user={UserID}");

        Field accountTypeString = new Field(ACCOUNT_TYPE+"String", "Account.type", FieldType.String);
        accountTypeString.setFilterable(false);
        accountTypeString.setVisible(false);
        accountTypeString.setImportance(FieldImportance.Required);
        fields.put(accountTypeString.getName().toUpperCase(), accountTypeString);

        Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setTranslationPrefixAndSuffix("AccountType", "");
        accountType.setDrillDownField(accountTypeString.getName());
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

        Field accountManager = new Field("UserGroup","User.id",FieldType.UserGroup);
        accountManager.setVisible(false);
        accountManager.setPrefixValue("SELECT userID FROM calc_inherited_user_group ciug " +
                "WHERE groupID IN ");
        accountManager.setSuffixValue("");
        fields.put(accountManager.getName().toUpperCase(), accountManager);

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