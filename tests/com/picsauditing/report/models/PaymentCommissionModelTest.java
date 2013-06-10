package com.picsauditing.report.models;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Filter;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.picsauditing.util.Assert.assertContains;
import static com.picsauditing.util.Assert.assertNotContains;
import static junit.framework.Assert.assertEquals;

public class PaymentCommissionModelTest {
    private PaymentCommissionModel model;
    private Permissions permissions;

    @Before
    public void setUp() {
        permissions = EntityFactory.makePermission();
        model = new PaymentCommissionModel(permissions);
    }

    @Test
    public void testAvailableFields_activationPointsShouldBeRounded() throws Exception {
        EntityFactory.addUserPermission(permissions, OpPerms.SalesCommission);

        Field activationPointsField = model.getAvailableFields().get("PaymentCommissionActivationPoints".toUpperCase());
        String activationPointsDatabaseColumnName = activationPointsField.getDatabaseColumnName();
        assertEquals("ROUND(PaymentCommission.activationpoints, 2)", activationPointsDatabaseColumnName);
    }

    @Test
    public void testWhereClause_otherThanSalesRepsShouldNotBeRestricted() throws Exception {
        List<Filter> filters = new ArrayList<>();
        String whereClause = model.getWhereClause(filters);

        assertNotContains("AccountUser.userID = ", whereClause);
    }

    @Test
    public void testWhereClause_salesRepsShouldBeRestricted() throws Exception {
        permissions.getAllInheritedGroupIds().add(User.GROUP_SALES_REPS);
        List<Filter> filters = new ArrayList<>();
        String whereClause = model.getWhereClause(filters);

        assertContains("AccountUser.userID = ", whereClause);
    }

    @Test
    public void testWhereClause_salesRepsShouldBeRestrictedUnlessTheyreAlsoAManager() throws Exception {
        permissions.getAllInheritedGroupIds().add(User.GROUP_SALES_REPS);
        permissions.getAllInheritedGroupIds().add(User.GROUP_MANAGER);
        List<Filter> filters = new ArrayList<>();
        String whereClause = model.getWhereClause(filters);

        assertNotContains("AccountUser.userID = ", whereClause);
    }
}
