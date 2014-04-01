package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class OperatorsModel extends AbstractModel {

	public static final String ACCOUNT_TYPE = "AccountType";

	public OperatorsModel(Permissions permissions) {
		super(permissions, new AccountTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Account");
		ModelSpec operator = spec.join(AccountTable.Operator);
        operator.alias = "Operator";
        operator.minimumImportance = FieldImportance.Low;
		ModelSpec reporting = operator.join(OperatorTable.Reporting);
		reporting.alias = "ReportingClient";

		ModelSpec parent = operator.join(OperatorTable.Parent);
		parent.alias = "ParentCorporation";
		parent.minimumImportance = FieldImportance.Required;

		ModelSpec parentOperator = parent.join(AccountTable.Operator);
		parentOperator.alias = "parentOperator";
        parentOperator.minimumImportance = FieldImportance.Required;

        ModelSpec accountManager = spec.join(AccountTable.AccountManager);
        accountManager.minimumImportance = FieldImportance.Required;
        accountManager.alias = "AccountManager";

		ModelSpec accountManagerUser = accountManager.join(AccountUserTable.User);
		accountManagerUser.alias = "AccountManagerUser";
		accountManagerUser.minimumImportance = FieldImportance.Required;

		ModelSpec salesRep = spec.join(AccountTable.SalesRep);
		salesRep.minimumImportance = FieldImportance.Required;
		salesRep.alias = "SalesRep";

		ModelSpec salesRepUser = salesRep.join(AccountUserTable.User);
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

        Field accountTypeString = new Field(ACCOUNT_TYPE+"String", "Account.type", FieldType.String);
        accountTypeString.setFilterable(false);
        accountTypeString.setVisible(false);
        accountTypeString.setImportance(FieldImportance.Required);
        fields.put(accountTypeString.getName().toUpperCase(), accountTypeString);

		Field accountType = new Field(ACCOUNT_TYPE, "Account.type", FieldType.AccountType);
		accountType.setTranslationPrefixAndSuffix(ACCOUNT_TYPE, "");
        accountType.setDrillDownField(accountTypeString.getName());
		fields.put(ACCOUNT_TYPE.toUpperCase(), accountType);

		return fields;
	}
}