package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.report.fields.Field;

public class AccountContractorAuditOperatorModelTest extends ModelTest {

	private AccountContractorAuditOperatorModel model;

	@Before
	public void setUp() {
		super.setUp();

		EntityFactory.addUserPermission(permissions, OpPerms.AllContractors);

		model = new AccountContractorAuditOperatorModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();
		
		excludedFields.add("ContractorOperatorFlagColor");
		excludedFields.add("AccountContactName");

		includedFields.add("AuditID");
		includedFields.add("AuditClosingAuditorName");
		includedFields.add("ContractorRenew");

		checkFields();
	}

	@Test
	public void testAccountContractorAuditOperatorModel_FieldNamesShouldNotChange() {
		// If you change these values, you will break reports in the database because we
		// store the field name and use it to lookup an available field.
		// If you must change these values, change the entities in the database as well.
		assertEquals("ContractorsRequiringAnnualUpdateEmail", AccountContractorAuditOperatorModel.CONTRACTORS_REQUIRING_ANNUAL_UPDATE_EMAIL);
	}

	@Test
	public void testAccountContractorAuditOperatorModel_FieldIndexedByExpectedKey() {
		Map<String, Field> fields = model.getAvailableFields();

		Field field = fields.get("ContractorsRequiringAnnualUpdateEmail".toUpperCase());
		assertNotNull(field);
		assertEquals("ContractorsRequiringAnnualUpdateEmail", field.getName());
	}

}
