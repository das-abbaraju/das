package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.ContractorAuditFileTable;
import com.picsauditing.report.tables.ContractorAuditTable;
import com.picsauditing.report.tables.ContractorTable;
import com.picsauditing.report.tables.FieldCategory;

import java.util.List;
import java.util.Map;

public class ContractorAuditFileModel extends AbstractModel {

	public static final String ContractorAuditFile = "ContractorAuditFile";

	public ContractorAuditFileModel(Permissions permissions) {
		super(permissions, new ContractorAuditFileTable());
	}

	public ModelSpec getJoinSpec() {
		// join() param is the name of the ReportForeignKey in the "from" table that points to the table we want to join to

		ModelSpec contractorAuditFile = new ModelSpec(null, ContractorAuditFile, FieldCategory.Audits);

		// join from ContractorAuditFileTable to ContractorAuditTable
		ModelSpec contractorAudit = contractorAuditFile.join(ContractorAuditFileTable.ContractorAudit);
		contractorAudit.alias = "Audit";    // Needs to agree with "AuditExpiresDate" in order by

		// We need to join from ContractorAuditTable to ContractorAccountTable, but since that join is not available in
		// ContractorAuditTable, we'll join to ContractorTable instead (which is actually the contractor_info db table)
		// as an intermediate step.
		ModelSpec contractorInfo = contractorAudit.join(ContractorAuditTable.Contractor);
		contractorInfo.alias = "ContractorInfo";

		// Now, join from ContractorTable to ContractorAccountTable
		ModelSpec contractorAccount = contractorInfo.join(ContractorTable.Account);
		contractorAccount.alias = "Account"; // Needs to agree to the param in the setUrl method below

		return contractorAuditFile;
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
		accountName.setUrl("ContractorView.action?id={AccountID}");
		return fields;
	}
}
