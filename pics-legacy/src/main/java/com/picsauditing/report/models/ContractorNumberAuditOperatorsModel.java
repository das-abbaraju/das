package com.picsauditing.report.models;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

import java.util.List;
import java.util.Map;

public class ContractorNumberAuditOperatorsModel extends AbstractModel {
    public static final String CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL = "ContractorsRequiringAnnualUpdateEmail";
    private static final String AUDIT_OPERATOR = "AuditOperator";
    private static final String CONTRACTOR_OPERATOR = "ContractorOperator";

    public ContractorNumberAuditOperatorsModel(Permissions permissions) {
        super(permissions, new ContractorAuditOperatorTable());
    }

    public ModelSpec getJoinSpec() {
        ModelSpec spec = new ModelSpec(null, AUDIT_OPERATOR);

        ModelSpec operatorAccount = spec.join(ContractorAuditOperatorTable.Operator);
        operatorAccount.alias = "AuditOperatorAccount";

        ModelSpec conAudit = spec.join(ContractorAuditOperatorTable.Audit);
        conAudit.alias = "Audit";
        conAudit.join(ContractorAuditTable.Type);
        conAudit.join(ContractorAuditTable.Auditor);
        conAudit.join(ContractorAuditTable.ClosingAuditor);

        ModelSpec previousAudit = conAudit.join(ContractorAuditTable.PreviousAudit);
        previousAudit.alias = "PreviousAudit";
        previousAudit.minimumImportance = FieldImportance.Average;

        ModelSpec contractor = conAudit.join(ContractorAuditTable.Contractor);
        contractor.alias = "Contractor";
        contractor.minimumImportance = FieldImportance.Average;
        ModelSpec account = contractor.join(ContractorTable.Account);
        account.alias = AbstractModel.ACCOUNT;
        account.minimumImportance = FieldImportance.Average;
        account.join(AccountTable.Contact);

        account.join(AccountTable.Contact);
        contractor.join(ContractorTable.Tag);

        ModelSpec customerService = contractor.join(ContractorTable.CustomerService);
        customerService.alias = "CustomerService";
        customerService.minimumImportance = FieldImportance.Required;

        ModelSpec customerServiceUser = customerService.join(AccountUserTable.User);
        customerServiceUser.alias = "CustomerServiceUser";

        ModelSpec pqf = contractor.join(ContractorTable.PQF);
        pqf.alias = "PQF";
        pqf.minimumImportance = FieldImportance.Required;
        ModelSpec manual = pqf.join(ContractorAuditTable.SafetyManual);
        manual.alias = "SafetyManual";
        manual.minimumImportance = FieldImportance.Average;

        if (permissions.isOperatorCorporate()) {
            ModelSpec flag = contractor.join(ContractorTable.Flag);
            flag.alias = CONTRACTOR_OPERATOR;
            flag.minimumImportance = FieldImportance.Average;
            flag.join(ContractorOperatorTable.Number);
        }

        conAudit.join(ContractorAuditTable.UsFatalities);
        conAudit.join(ContractorAuditTable.UsTrir);
        conAudit.join(ContractorAuditTable.UsLwcr);
        conAudit.join(ContractorAuditTable.CaFatalities);
        conAudit.join(ContractorAuditTable.CaTrir);
        conAudit.join(ContractorAuditTable.CaLwcr);
        conAudit.join(ContractorAuditTable.Afr);
        conAudit.join(ContractorAuditTable.Air);

        ModelSpec emr = conAudit.join(ContractorAuditTable.Emr);
        emr.alias = "Emr";

        return spec;
    }

    @Override
    public String getWhereClause(List<Filter> filters) {
        super.getWhereClause(filters);
        permissionQueryBuilder.setContractorOperatorAlias(CONTRACTOR_OPERATOR);

        String where = permissionQueryBuilder.buildWhereClause();

        if (!where.isEmpty()) {
            where += " AND ";
        }

        where += "AuditOperator.visible = 1";

        if (permissions.isOperatorCorporate()) {
            // TODO: This looks like it can be further improved. Find a way to
            // do this without having to implode all of the ids.
            String opIDs = permissions.getAccountIdString();
            if (permissions.isCorporate()) {
                opIDs = Strings.implode(permissions.getOperatorChildren());
            }

            where += "\n AND " + AUDIT_OPERATOR
                    + ".id IN (SELECT caoID FROM contractor_audit_operator_permission WHERE opID IN (" + opIDs + "))";
        }

        return where;
    }

    @Override
    public Map<String, Field> getAvailableFields() {
        Map<String, Field> fields = super.getAvailableFields();
        Field accountName = fields.get("AccountName".toUpperCase());
        accountName.setUrl("ContractorView.action?id={AccountID}");

        Field usFatalities = fields.get("AuditCaFatalitiesAnswer".toUpperCase());
        usFatalities.setDatabaseColumnName("REPLACE(" + usFatalities.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        usFatalities.setType(FieldType.Number);
        Field usTrir = fields.get("AuditUsTrirAnswer".toUpperCase());
        usTrir.setDatabaseColumnName("REPLACE(" + usTrir.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        usTrir.setType(FieldType.Number);
        Field emr = fields.get("EmrAnswer".toUpperCase());
        emr.setDatabaseColumnName("REPLACE(" + emr.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        emr.setType(FieldType.Number);
        Field usLwcr = fields.get("AuditUsLwcrAnswer".toUpperCase());
        usLwcr.setDatabaseColumnName("REPLACE(" + usLwcr.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        usLwcr.setType(FieldType.Number);
        Field caFatalities = fields.get("AuditCaFatalitiesAnswer".toUpperCase());
        caFatalities.setDatabaseColumnName("REPLACE(" + caFatalities.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        caFatalities.setType(FieldType.Number);
        Field caTrir = fields.get("AuditCaTrirAnswer".toUpperCase());
        caTrir.setDatabaseColumnName("REPLACE(" + caTrir.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        caTrir.setType(FieldType.Number);
        Field caLwcr = fields.get("AuditCaLwcrAnswer".toUpperCase());
        caLwcr.setDatabaseColumnName("REPLACE(" + caLwcr.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        caLwcr.setType(FieldType.Number);
        Field air = fields.get("AuditAirAnswer".toUpperCase());
        air.setDatabaseColumnName("REPLACE(" + air.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        air.setType(FieldType.Number);
        Field afr = fields.get("AuditAfrAnswer".toUpperCase());
        afr.setDatabaseColumnName("REPLACE(" + afr.getDatabaseColumnName() + ",'Audit.missingParameter','')");
        afr.setType(FieldType.Number);

        Field contractorsRequiringAnnualUpdateEmail = new Field(
                CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL,
                "(Audit.auditTypeID = 1 AND AuditOperator.status = 'Resubmit') " +
                        "OR (Audit.auditTypeID = 11 AND Audit.auditFor = " + getLastYear() + " AND AuditOperator.status = 'Pending') " +
                        "OR (AuditType.classType = 'PQF' AND DATE(Audit.expiresDate) BETWEEN '" + getThisYear() + "-02-28' AND '" + getThisYear() + "-04-01' AND AuditOperator.status = 'Pending')",
                FieldType.Boolean);
        contractorsRequiringAnnualUpdateEmail.requirePermission(OpPerms.DevelopmentEnvironment);
        fields.put(CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL.toUpperCase(), contractorsRequiringAnnualUpdateEmail);

        Field clientSite = new Field("ClientSite","Account.id",FieldType.Operator);
        clientSite.setVisible(false);
        clientSite.setPrefixValue("SELECT co.conID " +
                "FROM contractor_operator co " +
                "WHERE co.opID IN ");
        clientSite.setSuffixValue("");
        fields.put(clientSite.getName().toUpperCase(), clientSite);

        Field verify = new Field("PQFVerification","'Verify'",FieldType.Operator);
        verify.setFilterable(false);
        verify.setUrl("VerifyView.action?id={AccountID}");
        verify.requirePermission(OpPerms.AuditVerification);
        fields.put(verify.getName().toUpperCase(), verify);

        Field policyEffectiveDate = new Field("PolicyEffectiveDate","(SELECT p.answer FROM pqfdata p " +
                "JOIN audit_question q ON p.questionID = q.id AND q.uniqueCode = 'policyEffectiveDate' " +
                "JOIN audit_category c ON q.categoryID = c.id WHERE p.auditID = Audit.id)",FieldType.Date);
        fields.put(policyEffectiveDate.getName().toUpperCase(), policyEffectiveDate);
        Field policyExpirationDate = new Field("PolicyExpirationDate","(SELECT p.answer FROM pqfdata p " +
                "JOIN audit_question q ON p.questionID = q.id AND q.uniqueCode = 'policyExpirationDate' " +
                "JOIN audit_category c ON q.categoryID = c.id WHERE p.auditID = Audit.id)",FieldType.Date);
        fields.put(policyExpirationDate.getName().toUpperCase(), policyExpirationDate);
        Field eachOccurence = new Field("EachOccurence","(SELECT p.answer FROM pqfdata p " +
                "JOIN audit_question q ON p.questionID = q.id AND q.id IN " +
                "(2074,2149,2155,2161,2167,2173,2179,2185,2230,2284,2292,3029,10503,10504,10505,13021,13065,14317,14323,14337,14341,14421,18474,20895,21568,23386) " +
                "JOIN audit_category c ON q.categoryID = c.id WHERE p.auditID = Audit.id)",FieldType.Date);
        fields.put(eachOccurence.getName().toUpperCase(), eachOccurence);

        return fields;
    }

    private String getLastYear() {
        return String.valueOf(org.joda.time.DateTime.now().minusYears(1).getYear());
    }

    private String getThisYear() {
        return String.valueOf(org.joda.time.DateTime.now().getYear());
    }
}