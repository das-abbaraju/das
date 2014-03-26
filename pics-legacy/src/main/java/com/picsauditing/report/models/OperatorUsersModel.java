package com.picsauditing.report.models;


import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

public class OperatorUsersModel extends AbstractModel {

    public static final String ACCOUNT_TYPE = "AccountType";

	public OperatorUsersModel(Permissions permissions) {
        super(permissions, new UserTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec spec = new ModelSpec(null, "User");

        ModelSpec account = spec.join(UserTable.Account);
        account.alias = "Account";

        ModelSpec operator = account.join(AccountTable.Operator);
        operator.minimumImportance = FieldImportance.Low;
        operator.alias = "Operator";

        ModelSpec loginLog = spec.join(UserTable.LoginLog);
        loginLog.alias = "LoginLog";

        ModelSpec userGroup = spec.join(UserTable.Group);
        userGroup.alias = "UserGroup";

        ModelSpec group = userGroup.join(UserGroupTable.Group);
        group.alias = "Groups";

        return spec;
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();

        Field accountTypeString = new Field(ACCOUNT_TYPE+"String", "Account.type", FieldType.String);
        accountTypeString.setFilterable(false);
        accountTypeString.setVisible(false);
        accountTypeString.setImportance(FieldImportance.Required);
        fields.put(accountTypeString.getName().toUpperCase(), accountTypeString);

        Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setTranslationPrefixAndSuffix("AccountType", "");
        accountType.setDrillDownField(accountTypeString.getName());
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?user={UserID}");

		return fields;
    }
}