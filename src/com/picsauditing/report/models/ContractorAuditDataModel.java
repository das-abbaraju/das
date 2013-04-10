package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;

import java.util.List;
import java.util.Map;

public class ContractorAuditDataModel extends AbstractModel {

    public ContractorAuditDataModel(Permissions permissions) {
        super(permissions, new AuditDataTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec auditData = new ModelSpec(null, "AuditData");
        auditData.category = FieldCategory.Audits;

        ModelSpec conAudit = auditData.join(AuditDataTable.Audit);
        conAudit.alias = "Audit";
        ModelSpec type = conAudit.join(ContractorAuditTable.Type);
        type.alias = "AuditType";
        type.minimumImportance = FieldImportance.Required;

        ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
        contractor.alias = "Contractor";

        ModelSpec account = contractor.join(ContractorTable.Account);
        account.alias = "Account";
        account.minimumImportance = FieldImportance.Average;

        ModelSpec flagCriteriaContractor = contractor.join(ContractorTable.FlagCriteriaContractor);
        flagCriteriaContractor.alias = "FlagCriteriaContractor";
        flagCriteriaContractor.category = FieldCategory.Audits;

        ModelSpec question = auditData.join(AuditDataTable.Question);
        question.alias = "Question";
        question.category = FieldCategory.Audits;

        ModelSpec category = question.join(AuditQuestionTable.Category);
        category.alias = "Category";
        category.category = FieldCategory.Audits;

        return auditData;
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

        Field auditDataAnswer = fields.get("AuditDataAnswer".toUpperCase());
        auditDataAnswer.setType(FieldType.Float);

        return fields;
    }
}