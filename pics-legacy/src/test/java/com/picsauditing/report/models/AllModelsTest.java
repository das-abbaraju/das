package com.picsauditing.report.models;

import com.google.common.base.Joiner;
import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dr.domain.fields.QueryFilterOperator;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.fields.Field;
import com.picsauditing.search.SelectSQL;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@UseReporter(DiffReporter.class)
public class AllModelsTest {

    private AbstractModel model;
    private Permissions permissions;

    @Before
    public void setUp() {
        permissions = EntityFactory.makePermission();
    }

    @Test
    public void testAllModels() throws Exception {
        StringBuilder actual = new StringBuilder();

        for (ModelType type : ModelType.values()) {
            actual.append(String.format("Model: %s\nPermission: %s\n----------------------------------------\n", type, permissions));
            permissions = EntityFactory.makePermission();
            model = ReportModelFactory.build(type, permissions);
            if (model != null) {
                actual.append(getJoin());
            } else {
                actual.append("Not Yet Implemented");
            }
            actual.append("\n\n");
        }
        Approvals.verify(actual.toString());
    }

    @Test
    @Ignore
    public void testAllSql() throws Exception {
        SqlBuilder builder = new SqlBuilder();
        StringBuilder actual = new StringBuilder();

        for (ModelType type : ModelType.values()) {
            actual.append(String.format("Model: %s\nPermission: %s\n----------------------------------------\n", type, permissions));
            permissions = EntityFactory.makePermission();
            try {
                final Report report = new Report();
                model = ReportModelFactory.build(type, permissions);
                report.setModelType(type);
                final SelectSQL selectSQL = builder.initializeReportAndBuildSql(report, permissions);
                actual.append(selectSQL.toString());
            } catch (Exception e) {
                actual.append("Not Yet Implemented");
            }
            actual.append("\n\n");
        }
        Approvals.verify(actual.toString());
    }

    @Test
    public void testContractorTradesModel_Operator() throws Exception {
        permissions.setAccountType("Corporate");
        model = new ContractorTradesModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorsModelWhereClause_ArchivedReportDeactivatedContractorsVisibleForOperator() throws Exception {
        permissions.setAccountType("Corporate");
        model = new ContractorsModel(permissions);
        List<Filter> filters = new ArrayList<>();
        Filter filter = new Filter();
        filter.setField(new Field("AccountStatus"));
        filter.setName("ACCOUNTSTATUS");
        filter.setOperator(QueryFilterOperator.In);
        filter.setValue("Deactivated");
        filters.add(filter);
        assertEquals("Account.status IN ('Active','Deactivated')", model.getWhereClause(filters));
    }

    @Test
    public void testContractorsModel_Admin() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
        EntityFactory.addUserPermission(permissions, OpPerms.RiskRank);
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        permissions.setAccountType("Admin");
        model = new ContractorsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorsModel_LicenseReport() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.ContractorLicenseReport);
        permissions.setAccountType("Admin");
        model = new ContractorsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorsModel_Billing() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.Billing);
        permissions.setAccountType("Admin");
        model = new ContractorsModel(permissions);
        String output = "Permissions: " + permissions + "\n" + getJoin();
        Approvals.verify(output);
    }

    @Test
    public void testContractorsModel_Corporate() throws Exception {
        permissions.setAccountType("Corporate");
        model = new ContractorsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorAuditOperatorsModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new ContractorAuditOperatorsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorFeesModel_Billing() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.Billing);
        permissions.setAccountType("Admin");
        model = new ContractorFeesModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testInvoiceModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new InvoicesModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testInvoiceModel_UrlOperator() throws Exception {
        model = new InvoicesModel(permissions);
        Map<String, Field> fields = model.getAvailableFields();
        Field accountName = fields.get("AccountName".toUpperCase());
        assertTrue(accountName.getUrl().startsWith("ContractorView"));
    }

    @Test
    public void testInvoiceModel_UrlAdmin() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new InvoicesModel(permissions);
        Map<String, Field> fields = model.getAvailableFields();
        Field accountName = fields.get("AccountName".toUpperCase());
        assertTrue(accountName.getUrl().startsWith("BillingDetail"));
    }

    @Test
    public void testPaymentModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new PaymentsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testGetWhereClause_COModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new ContractorOperatorsModel(permissions);
        assertEquals("", model.getWhereClause(new ArrayList<Filter>()));
    }

    @Test
    public void testGetWhereClause_COModel_asOperator() throws Exception {
        permissions.setAccountType("Operator");
        model = new ContractorOperatorsModel(permissions);
        assertEquals("Account.status IN ('Active') AND ContractorOperator.opID IN (1100) AND ContractorOperator.workStatus = 'Y'", model.getWhereClause(new ArrayList<Filter>()));
    }

    @Test
    public void testGetWhereClause_COModel_asCorporate() throws Exception {
        permissions.setAccountType("Corporate");
        model = new ContractorOperatorsModel(permissions);
        assertEquals("Account.status IN ('Active') AND ContractorOperator.opID IN ()", model.getWhereClause(new ArrayList<Filter>()));
    }

    @Test
    public void testPOCModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.SalesCommission);
        model = new PaymentOperatorCommissionsModel(permissions);
        Approvals.verify(getJoin());
    }

    private String getJoin() {
        List<String> fieldValues = new ArrayList<String>();

        for (Field field : model.getAvailableFields().values()) {
            fieldValues.add(field.getName());
        }
        Collections.sort(fieldValues);
        return Joiner.on("\n").join(fieldValues);
    }

}
