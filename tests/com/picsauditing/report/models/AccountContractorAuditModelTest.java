package com.picsauditing.report.models;

import org.junit.Before;
import org.junit.Test;

public class AccountContractorAuditModelTest extends ModelTest {
	private AccountContractorAuditModel model;

	@Before
	public void setUp() {
		super.setUp();
		model = new AccountContractorAuditModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		excludedFields.add("ContractorPQFExpiresDate");
		excludedFields.add("ContractorOperatorFlagColor");

		includedFields.add("AuditID");
		includedFields.add("AuditEffectiveDate");
		includedFields.add("AuditAuditorName");
		includedFields.add("AuditClosingAuditorName");
		includedFields.add("AccountLegalName");

		checkFields();
	}

}
