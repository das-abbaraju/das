package com.picsauditing.report.models;

import org.junit.Before;
import org.junit.Test;

public class PaymentCommissionModelTest extends ModelTest {
	private PaymentCommissionModel model;

	@Before
	public void setup() {
		super.setup();
		model = new PaymentCommissionModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		excludedFields.add("AccountFax");
		includedFields.add("InvoiceCommissionRecipientUserName");
		includedFields.add("InvoiceDueDate");
		includedFields.add("AccountContactEmail");
		includedFields.add("PaymentCommissionPaymentCheckNumber");
		
		checkFields();
	}
}
