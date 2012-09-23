package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ContractorOperatorModelTest extends ModelTest {
	private ContractorOperatorModel model;

	@Before
	public void setup() {
		super.setup();
		model = new ContractorOperatorModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();
		assertEquals("OK if close to expected because we added a few fields", 69, availableFields.size());
	}

}
