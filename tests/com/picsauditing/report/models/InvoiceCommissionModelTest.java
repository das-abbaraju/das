package com.picsauditing.report.models;

import org.junit.Before;
import org.junit.Test;

public class InvoiceCommissionModelTest extends ModelTest {
	private InvoiceCommissionModel model;

	@Before
	public void setup() {
		super.setup();
		model = new InvoiceCommissionModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		excludedFields.add("AccountFax");
		includedFields.add("InvoiceCommissionRecipientUserName");
	}

}
