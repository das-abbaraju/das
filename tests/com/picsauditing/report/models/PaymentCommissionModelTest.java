package com.picsauditing.report.models;

import org.junit.Before;
import org.junit.Ignore;

public class PaymentCommissionModelTest extends ModelTest {
	private PaymentCommissionModel model;

	@Before
	public void setup() {
		super.setup();
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
}
