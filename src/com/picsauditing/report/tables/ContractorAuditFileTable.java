package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAuditFile;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorAuditFileTable extends AbstractTable {

	public static final String ContractorAudit = "ContractorAudit";

	public ContractorAuditFileTable() {
		super("contractor_audit_file");
		addFields(ContractorAuditFile.class);
		Field id = addPrimaryKey();
		id.setCategory(FieldCategory.Audits);

		Field creationDate = new Field("UploadDate", "creationDate", FieldType.Date);
		addField(creationDate).setCategory(FieldCategory.Audits);
	}

	public void addJoins() {
		ReportForeignKey fkToContractorAudit = new ReportForeignKey(ContractorAudit, new ContractorAuditTable(), new ReportOnClause("auditID", "id"));
		fkToContractorAudit.setCategory(FieldCategory.Audits);
		addJoinKey(fkToContractorAudit);
	}

}
