package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;

public class AccountOperatorModelTest {

	@Test
	public void testAvailableFields() throws Exception {
		AccountOperatorModel model = new AccountOperatorModel();
		Permissions permissions = EntityFactory.makePermission();

		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);

		assertEquals("OK if close to expected because we added a few fields", 39, availableFields.size());
	}

}
