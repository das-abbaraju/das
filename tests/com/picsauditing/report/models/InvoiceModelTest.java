package com.picsauditing.report.models;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;

public class InvoiceModelTest extends ModelTest {
	private InvoiceModel model;

	@Before
	public void setUp() {
		super.setUp();
		EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);
		model = new InvoiceModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		includedFields.add("AccountName");
		includedFields.add("AccountContactEmail");
		includedFields.add("ContractorBalance");
		includedFields.add("InvoicePoNumber");
		includedFields.add("AccountCountry");
		
		excludedFields.add("AccountReason");

		checkFields();
	}

}
