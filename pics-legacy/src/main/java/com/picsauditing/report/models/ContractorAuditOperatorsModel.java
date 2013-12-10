package com.picsauditing.report.models;

import java.util.List;
import java.util.Map;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.tables.*;
import com.picsauditing.util.Strings;

public class ContractorAuditOperatorsModel extends AbstractModel {
    public static final String CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL = "ContractorsRequiringAnnualUpdateEmail";
    private static final String AUDIT_OPERATOR = "AuditOperator";
    private static final String CONTRACTOR_OPERATOR = "ContractorOperator";

    public ContractorAuditOperatorsModel(Permissions permissions) {
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
        }

        ModelSpec fatalities = conAudit.join(ContractorAuditTable.Fatalities);
        fatalities.alias = "Fatalities";

        conAudit.join(ContractorAuditTable.UsTrir);
        conAudit.join(ContractorAuditTable.UsLwcr);
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

        Field fatalities = fields.get("FatalitiesAnswer".toUpperCase());
        fatalities.setType(FieldType.Number);
        Field usTrir = fields.get("AuditUsTrirAnswer".toUpperCase());
        usTrir.setType(FieldType.Number);
        Field emr = fields.get("EmrAnswer".toUpperCase());
        emr.setType(FieldType.Number);
        Field usLwcr = fields.get("AuditUsLwcrAnswer".toUpperCase());
        usLwcr.setType(FieldType.Number);
        Field caTrir = fields.get("AuditCaTrirAnswer".toUpperCase());
        caTrir.setType(FieldType.Number);
        Field caLwcr = fields.get("AuditCaLwcrAnswer".toUpperCase());
        caLwcr.setType(FieldType.Number);
        Field air = fields.get("AuditAirAnswer".toUpperCase());
        air.setType(FieldType.Number);
        Field afr = fields.get("AuditAfrAnswer".toUpperCase());
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

        return fields;
    }

    private String getLastYear() {
        return "2013";
    }

    private String getThisYear() {
        return "2014";
    }
}