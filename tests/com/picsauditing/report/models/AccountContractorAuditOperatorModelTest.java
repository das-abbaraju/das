package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class AccountContractorAuditOperatorModelTest extends ModelTest {
	private AccountContractorAuditOperatorModel model;

	@Before
	public void setup() {
		super.setup();
		model = new AccountContractorAuditOperatorModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();
		assertFalse("accountContactName was removed", availableFields.containsKey("accountContactName".toUpperCase()));
		assertEquals("OK if close to expected because we added a few fields", 42, availableFields.size());
	}

}
