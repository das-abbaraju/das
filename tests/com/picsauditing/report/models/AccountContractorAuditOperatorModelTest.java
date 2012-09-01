package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;

public class AccountContractorAuditOperatorModelTest {
	@Test
	public void testAvailableFields() throws Exception {
		AccountContractorAuditOperatorModel model = new AccountContractorAuditOperatorModel();
		Permissions permissions = EntityFactory.makePermission();

		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);

		assertFalse("accountContactName was removed", availableFields.containsKey("accountContactName".toUpperCase()));

		assertEquals("OK if close to expected because we added a few fields", 44, availableFields.size());
	}

}
