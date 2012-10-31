package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.UserTable;
import com.picsauditing.util.Strings;

public class UserModel extends AbstractModel {
	public UserModel(Permissions permissions) {
		super(permissions, new UserTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "User");
		spec.category = FieldCategory.AccountInformation;
		spec.join(UserTable.Account).alias = "Account";
		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
	
		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("UsersManage.action?account={AccountID}");
		Field userName = fields.get("UserName".toUpperCase());
		userName.setUrl("UsersManage.action?account={AccountID}&user={UserID}");
		
		Field accountType = new Field("AccountType", "type", FieldType.AccountType);
		accountType.setCategory(FieldCategory.AccountInformation);
		accountType.setTranslationPrefixAndSuffix("AccountType", "");
		fields.put(accountType.getName().toUpperCase(), accountType);
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