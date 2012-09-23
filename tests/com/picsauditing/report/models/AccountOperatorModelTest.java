package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;

public class AccountOperatorModelTest {

	@Test
	public void testAvailableFields() throws Exception {
		Permissions permissions = EntityFactory.makePermission();
		AccountOperatorModel model = new AccountOperatorModel(permissions);

		Map<String, Field> availableFields = model.getAvailableFields();

		assertEquals("OK if close to expected because we added a few fields", 32, availableFields.size());
	}

}
