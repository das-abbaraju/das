package com.picsauditing.report.models;

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

		includedFields.add("ContractorOperatorOperatorID");
		includedFields.add("ContractorOperatorOperatorName");
		includedFields.add("ContractorOperatorForceFlag");
		includedFields.add("AccountStatus");
		includedFields.add("AccountID");
		includedFields.add("AccountName");

//		excludedFields.add("AccountCountry");
		excludedFields.add("ContractorScore");
		excludedFields.add("ContractorOperatorOperatorStatus");
		checkFields();
	}

}
