package com.picsauditing.report.models;


import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.UserGroupTable;
import com.picsauditing.report.tables.UserTable;

public class OperatorUsersModel extends AbstractModel {

    public static final String ACCOUNT_TYPE = "AccountType";

	public OperatorUsersModel(Permissions permissions) {
        super(permissions, new UserTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec spec = new ModelSpec(null, "User");
        spec.category = FieldCategory.AccountInformation;

        ModelSpec account = spec.join(UserTable.Account);
        account.category = FieldCategory.AccountInformation;
        account.alias = "Account";

        ModelSpec operator = account.join(AccountTable.Operator);
        operator.category = FieldCategory.AccountInformation;
        operator.alias = "Operator";

        ModelSpec loginLog = spec.join(UserTable.LoginLog);
        loginLog.category = FieldCategory.AccountInformation;
        loginLog.alias = "LoginLog";

        ModelSpec userGroup = spec.join(UserTable.Group);
        userGroup.category = FieldCategory.AccountInformation;
        userGroup.alias = "UserGroup";

        ModelSpec group = userGroup.join(UserGroupTable.Group);
        group.category = FieldCategory.AccountInformation;
        group.alias = "Groups";

        return spec;
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();

        Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setCategory(FieldCategory.AccountInformation);
		accountType.setTranslationPrefixAndSuffix("AccountType", "");
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?user={UserID}");

		return fields;
    }
}