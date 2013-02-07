package com.picsauditing.report.models;


import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.UserGroupTable;
import com.picsauditing.report.tables.UserTable;

public class OperatorUserModel extends AbstractModel {

    public OperatorUserModel(Permissions permissions) {
        super(permissions, new UserTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec spec = new ModelSpec(null, "User");
        spec.category = FieldCategory.AccountInformation;
        
        ModelSpec operator = spec.join(UserTable.Account);
        operator.category = FieldCategory.AccountInformation;
        operator.alias = "Operator";

        ModelSpec loginLog = spec.join(UserTable.LoginLog);
        loginLog.category = FieldCategory.AccountInformation;
        loginLog.alias = "LoginLog";

        {
            ModelSpec userGroup = spec.join(UserTable.Group);
            userGroup.category = FieldCategory.AccountInformation;
            userGroup.alias = "UserGroup";
            {
                ModelSpec group = userGroup.join(UserGroupTable.Group);
                group.category = FieldCategory.AccountInformation;
                group.alias = "Groups";
            }
        }
        return spec;
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();

        Field accountType = new Field("OperatorType", "Operator.type", FieldType.AccountType);
		accountType.setCategory(FieldCategory.AccountInformation);
		accountType.setTranslationPrefixAndSuffix("AccountType", "");
		fields.put(accountType.getName().toUpperCase(), accountType);
		
		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?user={UserID}");
        
		return fields;
    }
}