package com.picsauditing.report.models;

import static com.picsauditing.util.Assert.assertNotContains;

import java.util.ArrayList;
import java.util.List;

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
	public void testWhereClause__NoAccountUserClause() throws Exception {
		List<Filter> filters = new ArrayList<Filter>();
		String whereClause = model.getWhereClause(filters);

		assertNotContains("AccountUser.userID = ", whereClause);
	}
}
