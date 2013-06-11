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
import java.util.List;

@UseReporter(DiffReporter.class)
public class AllModelsTest {

    private AbstractModel model;
    private Permissions permissions = EntityFactory.makePermission();

    @Test
    public void testAllModels() throws Exception {
        StringBuilder actual = new StringBuilder();

        for (ModelType type : ModelType.values()) {
            actual.append(String.format("Model: %s for permission: %s\n----------------------------------------\n", type,permissions));
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
    public void testAccountContractorModel_Admin() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        permissions.setAccountType("Admin");
        model = new ContractorsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testAccountContractorModel_Billing() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.Billing);
        permissions.setAccountType("Admin");
        model = new ContractorsModel(permissions);
        String output = "Permissions: " + permissions + "\n" + getJoin();
        Approvals.verify(output);
    }

    @Test
    public void testAccountContractorModel_Corporate() throws Exception {
        permissions.setAccountType("Corporate");
        model = new ContractorsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testAccountContractorAuditOperatorModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new ContractorAuditOperatorsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testInvoiceModel() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
        model = new InvoicesModel(permissions);
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
