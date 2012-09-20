package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;

public class AccountContractorAuditModelTest {

	@Test
	public void testAvailableFields() throws Exception {
		AccountContractorAuditModel model = new AccountContractorAuditModel();
		Permissions permissions = EntityFactory.makePermission();

		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);

		assertFalse("contractorPQFExpiresDate was removed",
				availableFields.containsKey("contractorPQFExpiresDate".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 74, availableFields.size());
	}

}
