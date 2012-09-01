package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.model.ReportModel;
import com.picsauditing.report.fields.Field;

public class AccountModelTest {

	@Test
	public void testAvailableFields() throws Exception {
		AccountModel model = new AccountModel();
		Permissions permissions = EntityFactory.makePermission();

		Map<String, Field> availableFields = ReportModel.buildAvailableFields(model.getRootTable(), permissions);
		
		assertFalse("User.isActive is Low importance", availableFields.containsKey("accountContactIsActive".toUpperCase()));
		assertTrue("User.email is Average importance", availableFields.containsKey("accountContactEmail".toUpperCase()));
		assertTrue("User.name is Required", availableFields.containsKey("accountContactName".toUpperCase()));
		
		assertEquals("OK if close to expected because we added a few fields", 31, availableFields.size());
	}

}
