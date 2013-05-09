package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class ContractorFeeModel extends AbstractModel {

	public ContractorFeeModel(Permissions permissions) {
		super(permissions, new ContractorFeeTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec contractorFee = new ModelSpec(null, "ContractorFee");

        ModelSpec contractor = contractorFee.join(ContractorFeeTable.Contractor);
        contractor.alias = "Contractor";

        ModelSpec account = contractor.join(ContractorTable.Account);
        account.alias = "Account";
        account.minimumImportance = FieldImportance.Average;

        ModelSpec fee = contractorFee.join(ContractorFeeTable.NewFee);
        fee.alias = "Fee";
        fee.minimumImportance = FieldImportance.Average;

		return contractorFee;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);

		return permissionQueryBuilder.buildWhereClause();
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("BillingDetail.action?id={AccountID}");

        return fields;
	}
}