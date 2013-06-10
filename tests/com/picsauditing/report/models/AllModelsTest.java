package com.picsauditing.report.models;

import com.google.common.base.Joiner;
import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@UseReporter(DiffReporter.class)
public class AllModelsTest {

    private AbstractModel model;
    private Permissions permissions = EntityFactory.makePermission();

    @Test
    public void testAccountsModel() throws Exception {
        model = new AccountsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testAccountContractorModel_Admin() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        permissions.setAccountType("Admin");
        model = new AccountContractorModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testAccountContractorModel_Corporate() throws Exception {
        permissions.setAccountType("Corporate");
        model = new AccountContractorModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testAccountContractorAuditModel() throws Exception {
        model = new AccountContractorAuditModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testAccountContractorAuditOperatorModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new AccountContractorAuditOperatorModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testAccountOperatorModel() throws Exception {
        model = new AccountOperatorModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorFlagDataModel() throws Exception {
        model = new ContractorFlagDataModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorOperatorModel() throws Exception {
        model = new ContractorOperatorModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testContractorAuditFileModel() throws Exception {
        model = new ContractorAuditFileModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testEmployeeCompetencyModel() throws Exception {
        model = new EmployeeCompetencyModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testInvoiceModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new InvoiceModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testOperatorAccountUserModel() throws Exception {
        model = new OperatorAccountUserModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testOperatorUserModel() throws Exception {
        model = new OperatorUserModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testPaymentCommissionModel() throws Exception {
        model = new PaymentCommissionModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testUserModel() throws Exception {
        model = new UserModel(permissions);
        Approvals.verify(getJoin());
    }

    private String getJoin() {
        List<Field> fieldValues = new ArrayList<Field>();
        fieldValues.addAll(model.getAvailableFields().values());
        Collections.sort(fieldValues, new Comparator<Field>() {
            public int compare(Field o1, Field o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return Joiner.on("\n").join(fieldValues);
    }

}
