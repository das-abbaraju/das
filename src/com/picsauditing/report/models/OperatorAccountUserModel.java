package com.picsauditing.report.models;

import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.AccountUserTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

public class OperatorAccountUserModel extends AbstractModel {

    public static final String ACCOUNT_TYPE = "AccountType";

	public OperatorAccountUserModel(Permissions permissions) {
        super(permissions, new AccountUserTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec spec = new ModelSpec(null, "AccountUser");
        spec.category = FieldCategory.AccountInformation;

        ModelSpec account = spec.join(AccountUserTable.Account);
        account.category = FieldCategory.AccountInformation;
        account.alias = "Account";

        ModelSpec user = spec.join(AccountUserTable.User);
        user.category = FieldCategory.ContactInformation;
        user.alias = "User";
        user.minimumImportance = FieldImportance.Required;

        return spec;
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();

        Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setCategory(FieldCategory.AccountInformation);
		accountType.setTranslationPrefixAndSuffix(ACCOUNT_TYPE, "");
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("FacilitiesEdit.action?operator={AccountID}");

		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?user={UserID}");

		return fields;
    }
}