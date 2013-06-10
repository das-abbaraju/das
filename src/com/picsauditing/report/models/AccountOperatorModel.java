package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class AccountOperatorModel extends AbstractModel {

	public static final String ACCOUNT_TYPE = "AccountType";

	public AccountOperatorModel(Permissions permissions) {
		super(permissions, new AccountTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Account");
		ModelSpec operator = spec.join(AccountTable.Operator);
        operator.alias = "Operator";
		ModelSpec reporting = operator.join(OperatorTable.Reporting);
		reporting.category = FieldCategory.ReportingClientSite;
        reporting.minimumImportance = FieldImportance.Required;
		reporting.alias = "ReportingClient";

		ModelSpec parent = operator.join(OperatorTable.Parent);
		parent.category = FieldCategory.AccountInformation;
		parent.alias = "ParentCorporation";
		parent.minimumImportance = FieldImportance.Required;

		ModelSpec parentOperator = parent.join(AccountTable.Operator);
		parentOperator.category = FieldCategory.AccountInformation;
		parentOperator.alias = "parentOperator";
        parentOperator.minimumImportance = FieldImportance.Required;

        ModelSpec accountManager = spec.join(AccountTable.AccountManager);
        accountManager.category = FieldCategory.CustomerService;
        accountManager.minimumImportance = FieldImportance.Required;
        accountManager.alias = "AccountManager";

		ModelSpec accountManagerUser = accountManager.join(AccountUserTable.User);
		accountManagerUser.category = FieldCategory.CustomerService;
		accountManagerUser.alias = "AccountManagerUser";
		accountManagerUser.minimumImportance = FieldImportance.Required;

		ModelSpec salesRep = spec.join(AccountTable.SalesRep);
		salesRep.category = FieldCategory.CustomerService;
		salesRep.minimumImportance = FieldImportance.Required;
		salesRep.alias = "SalesRep";

		ModelSpec salesRepUser = salesRep.join(AccountUserTable.User);
		salesRepUser.category = FieldCategory.CustomerService;
		salesRepUser.alias = "SalesRepUser";
		salesRepUser.minimumImportance = FieldImportance.Required;

        spec.join(AccountTable.LastLogin);

        return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);

		return permissionQueryBuilder.buildWhereClause(ModelType.Operators);
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("FacilitiesEdit.action?operator={AccountID}");

		Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setCategory(FieldCategory.AccountInformation);
		accountType.setTranslationPrefixAndSuffix(ACCOUNT_TYPE, "");
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

		return fields;
	}
}