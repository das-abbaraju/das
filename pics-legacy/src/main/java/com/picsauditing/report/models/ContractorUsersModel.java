package com.picsauditing.report.models;


import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldImportance;
import com.picsauditing.report.tables.UserGroupTable;
import com.picsauditing.report.tables.UserTable;

import java.util.List;
import java.util.Map;

public class ContractorUsersModel extends AbstractModel {

    public static final String ACCOUNT_TYPE = "AccountType";

	public ContractorUsersModel(Permissions permissions) {
        super(permissions, new UserTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec spec = new ModelSpec(null, "User");

        ModelSpec account = spec.join(UserTable.Account);
        account.alias = "Account";

        ModelSpec operator = account.join(AccountTable.Contractor);
        operator.minimumImportance = FieldImportance.Low;

        ModelSpec loginLog = spec.join(UserTable.LoginLog);
        loginLog.alias = "LoginLog";

        ModelSpec userGroup = spec.join(UserTable.Group);
        userGroup.alias = "UserGroup";

        ModelSpec group = userGroup.join(UserGroupTable.Group);
        group.alias = "Groups";

        return spec;
    }

    @Override
    public String getWhereClause(List<Filter> filters) {
        super.getWhereClause(filters);

        return permissionQueryBuilder.buildWhereClause();
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();

        Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setTranslationPrefixAndSuffix("AccountType", "");
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?user={UserID}");

		return fields;
    }
}