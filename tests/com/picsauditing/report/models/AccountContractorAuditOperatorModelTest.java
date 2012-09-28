package com.picsauditing.report.models;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.Column;

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

		excludedFields.add("ContractorOperatorFlagColor");
		
		excludedFields.add("AccountContactName");
		includedFields.add("AuditID");
		includedFields.add("AuditClosingAuditorName");

		checkFields();
	}

}
