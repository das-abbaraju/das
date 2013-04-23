package com.picsauditing.report.models;

import static com.picsauditing.util.Assert.assertContains;
import static com.picsauditing.util.Assert.assertEqualsToTheSecond;
import static com.picsauditing.util.Assert.assertNotContains;
import static junit.framework.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.UserAccess;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.fields.Field;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.picsauditing.jpa.entities.Filter;

public class PaymentCommissionModelTest extends ModelTest {
	private PaymentCommissionModel model;

	@Before
	public void setUp() {
		super.setUp();
		model = new PaymentCommissionModel(permissions);
	}

	@Ignore
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		excludedFields.add("AccountFax");
		includedFields.add("InvoiceCommissionRecipientUserName");
		includedFields.add("InvoiceDueDate");
		includedFields.add("AccountContactEmail");
		includedFields.add("PaymentCommissionPaymentCheckNumber");
		
		checkFields();
	}

	@Test
	public void testAvailableFields_activationPointsShouldBeRounded() throws Exception {
		setupSalesCommissionPermission();

		availableFields = model.getAvailableFields();

		Field activationPointsField = availableFields.get("PaymentCommissionActivationPoints".toUpperCase());
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
		model.permissions.getAllInheritedGroupIds().add(User.GROUP_SALES_REPS);
		List<Filter> filters = new ArrayList<>();
		String whereClause = model.getWhereClause(filters);

		assertContains("AccountUser.userID = ", whereClause);
	}

	@Test
	public void testWhereClause_salesRepsShouldBeRestrictedUnlessTheyreAlsoAManager() throws Exception {
		model.permissions.getAllInheritedGroupIds().add(User.GROUP_SALES_REPS);
		model.permissions.getAllInheritedGroupIds().add(User.GROUP_MANAGER);
		List<Filter> filters = new ArrayList<>();
		String whereClause = model.getWhereClause(filters);

		assertNotContains("AccountUser.userID = ", whereClause);
	}

	private void setupSalesCommissionPermission() {
		UserAccess userAccess = new UserAccess();
		userAccess.setOpPerm(OpPerms.SalesCommission);
		model.permissions.getPermissions().add(userAccess);
	}
}
