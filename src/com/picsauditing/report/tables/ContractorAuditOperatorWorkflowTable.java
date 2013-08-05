package com.picsauditing.report.tables;

import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;

public class ContractorAuditOperatorWorkflowTable extends AbstractTable {
    public static final String CAO = "Cao";
    public static final String User = "User";

	public ContractorAuditOperatorWorkflowTable() {
		super("contractor_audit_operator_workflow");
		Field primaryKey = addPrimaryKey();
		primaryKey.setCategory(FieldCategory.DocumentsAndAudits);

		addFields(ContractorAuditOperatorWorkflow.class);
	}

	protected void addJoins() {
        ReportForeignKey caoKey = addRequiredKey(new ReportForeignKey(CAO, new ContractorAuditOperatorTable(),
                new ReportOnClause("caoID")));
        caoKey.setCategory(FieldCategory.DocumentsAndAudits);
        caoKey.setMinimumImportance(FieldImportance.Required);

        addOptionalKey(new ReportForeignKey(User, new UserTable(), new ReportOnClause("createdBy", "id")));
    }
}