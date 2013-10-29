package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.ContractorAuditTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldImportance;

import java.util.List;
import java.util.Map;

public class ContractorAuditOperatorSuncorModel extends AbstractModel {
	public ContractorAuditOperatorSuncorModel(Permissions permissions) {
		super(permissions, new ContractorTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec spec = new ModelSpec(null, "Contractor");

        spec.join(ContractorTable.Account);

        ModelSpec suncorDAPolicy = spec.join(ContractorTable.SuncorDrugAlcoholPolicy);

        suncorDAPolicy.join(ContractorAuditTable.SingleCAO);

        for (Integer questionID : ContractorAuditTable.SUNCOR_DRUG_ALCOHOL_DATA.keySet()) {
            suncorDAPolicy.join(ContractorAuditTable.SUNCOR_DRUG_ALCOHOL_DATA.get(questionID));
        }

        return spec;
	}

	@Override
	public String getWhereClause(List<Filter> filters) {
		super.getWhereClause(filters);
        permissionQueryBuilder.setAccountAlias("ContractorAccount");

		String where = permissionQueryBuilder.buildWhereClause();

		if (!where.isEmpty()) {
			where += " AND ";
		}

		where += "ContractorSuncorDrugAlcoholPolicyCao.visible = 1";

		return where;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field accountName = fields.get("ContractorAccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={ContractorAccountID}");

        return fields;
	}
}