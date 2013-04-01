package com.picsauditing.report.models;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;

public class AccountContractorModelTest extends ModelTest {
	private AccountContractorModel model;

	@Before
	public void setUp() {
		super.setUp();
		model = new AccountContractorModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		excludedFields.add("ContractorRequestedByOperatorCity");
		includedFields.add("ContractorPQFID");
		includedFields.add("ContractorPQFTypeName");
		includedFields.add("ContractorProductRisk");
		includedFields.add("AccountFax");
		includedFields.add("AccountContactEmail");
		excludedFields.add("AccountContactLastLogin");
		includedFields.add("AccountNaicsTrir");
		// includedFields.add("ContractorCustomerServiceFax");
		excludedFields.add("ContractorCustomerServiceLastLogin");
		excludedFields.add("ContractorFlagFlagColor");
		checkFields();
	}

	@Test
	public void testAvailableFieldsForAdmin() throws Exception {
		EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		model = new AccountContractorModel(permissions);
		availableFields = model.getAvailableFields();

		includedFields.add("ContractorBalance");
		checkFields();
	}

}
