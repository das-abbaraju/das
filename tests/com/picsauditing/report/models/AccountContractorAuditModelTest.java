package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class AccountContractorAuditModelTest extends ModelTest {
	private AccountContractorAuditModel model;

	@Before
	public void setup() {
		super.setup();
		model = new AccountContractorAuditModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		assertFalse("contractorPQFExpiresDate was removed",
				availableFields.containsKey("contractorPQFExpiresDate".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 74, availableFields.size());
	}

}
