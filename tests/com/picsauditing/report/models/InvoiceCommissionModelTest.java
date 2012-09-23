package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class InvoiceCommissionModelTest extends ModelTest {
	private ContractorOperatorModel model;

	@Before
	public void setup() {
		super.setup();
		model = new ContractorOperatorModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		assertFalse("accountFax is Low importance", availableFields.containsKey("accountFax".toUpperCase()));
		assertTrue("invoiceCommissionRecipientUserName is required",
				availableFields.containsKey("invoiceCommissionRecipientUserName".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 20, availableFields.size());
	}

}
