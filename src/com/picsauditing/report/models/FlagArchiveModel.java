package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class FlagArchiveModel extends AbstractModel {

	public FlagArchiveModel(Permissions permissions) {
		super(permissions, new FlagArchiveTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "FlagArchive");
		ModelSpec opAccount = spec.join(FlagArchiveTable.Operator);
        opAccount.alias = "ContractorOperatorOperator";

        ModelSpec contractor = spec.join(FlagArchiveTable.Contractor);
		contractor.alias = "Contractor";

		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = "Account";

		return spec;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

        Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

        return fields;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
        super.getWhereClause(filters);

        String where = permissionQueryBuilder.buildWhereClause();

        if (!where.isEmpty()) {
            where += " AND ";
        }

        where += "DAY(FlagArchive.creationDate) = 1";

		return where;
	}
}