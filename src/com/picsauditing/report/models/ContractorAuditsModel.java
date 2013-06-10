package com.picsauditing.report.models;

import java.util.Map;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.ContractorAuditTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.report.tables.FieldImportance;

public class ContractorAuditsModel extends AbstractModel {
	public ContractorAuditsModel(Permissions permissions) {
		super(permissions, new ContractorAuditTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec conAudit = new ModelSpec(null, "Audit");

		ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
		contractor.alias = "Contractor";
		// We may not need this either if the Entity Fields are set
		// correctly
		// contractor.category = FieldCategory.AccountInformation;

		ModelSpec account = contractor.join(ContractorTable.Account);
		account.alias = "Account";
		account.minimumImportance = FieldImportance.Average;
		// We may not need this either if the Entity Fields are set
		// correctly
		// account.category = FieldCategory.AccountInformation;

		if (permissions.isOperatorCorporate()) {
			ModelSpec flag = contractor.join(ContractorTable.Flag);
			flag.alias = "ContractorOperator";
			flag.minimumImportance = FieldImportance.Average;
			flag.category = FieldCategory.AccountInformation;
		}

		conAudit.join(ContractorAuditTable.Auditor);
		conAudit.join(ContractorAuditTable.ClosingAuditor);
		conAudit.join(ContractorAuditTable.Type);

		return conAudit;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();

		Field accountName = fields.get("AccountName".toUpperCase());
		accountName.setUrl("ContractorView.action?id={AccountID}");

		return fields;
	}
}