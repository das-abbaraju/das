package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AccountOperatorModelTest extends ModelTest {
	private AccountOperatorModel model;

	@Before
	public void setup() {
		super.setup();
		model = new AccountOperatorModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		includedFields.add("AccountID");
		includedFields.add("AccountAddress3");
		includedFields.add("AccountNaicsCode");
		includedFields.add("OperatorAutoApproveInsurance");
		
		checkFields();
	}

}
