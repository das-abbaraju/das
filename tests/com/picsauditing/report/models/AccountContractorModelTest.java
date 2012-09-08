package com.picsauditing.report.models;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;

public class AccountContractorModelTest {

	private AccountContractorModel model = new AccountContractorModel();
	private Permissions permissions;

	@Before
	public void setup() {
		permissions = EntityFactory.makePermission();
	}

	@Test
	public void testAvailableFields() throws Exception {
		// EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
		// EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);
		
		assertFalse("contractorRequestedByOperatorCity is Low importance", availableFields.containsKey("contractorRequestedByOperatorCity".toUpperCase()));
		assertTrue("contractorPQFExpiresDate is Required", availableFields.containsKey("contractorPQFExpiresDate".toUpperCase()));
		
		assertEquals("OK if close to expected because we added a few fields", 58, availableFields.size());
	}

	@Test
	public void testAvailableFieldsForAdmin() throws Exception {
		EntityFactory.addUserPermission(permissions, OpPerms.AllOperators);
		EntityFactory.addUserPermission(permissions, OpPerms.Billing);
		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);
		
		assertTrue("contractorBalance is Required", availableFields.containsKey("contractorBalance".toUpperCase()));
		
		assertEquals("OK if close to expected because we added a few fields", 66, availableFields.size());
	}
}
