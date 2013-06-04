package com.picsauditing.report.models;

import com.google.common.base.Joiner;
import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import org.approvaltests.Approvals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AllModelsTest {

    private AbstractModel model;
    private Permissions permissions = EntityFactory.makePermission();

    @Test
    public void testAccountsModel() throws Exception {
        model = new AccountsModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testUserModel() throws Exception {
        model = new UserModel(permissions);
        Approvals.verify(getJoin());
    }

    @Test
    public void testEmployeeCompetencyModel() throws Exception {
        model = new EmployeeCompetencyModel(permissions);
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
