package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.FieldCategory;

public class AccountModelTest {

	@Test
	public void testAvailableFields() throws Exception {
		Permissions permissions = EntityFactory.makePermission();
		AccountModel model = new AccountModel(permissions);

		Map<String, Field> availableFields = model.getAvailableFields();

		assertFalse("User.isActive is Low importance",
				availableFields.containsKey("accountContactIsActive".toUpperCase()));
		assertTrue("User.email is Average importance", availableFields.containsKey("accountContactEmail".toUpperCase()));
		assertTrue("User.name is Required", availableFields.containsKey("accountContactName".toUpperCase()));
		assertTrue("User.ID is Required", availableFields.containsKey("accountContactID".toUpperCase()));
		assertEquals(FieldCategory.ContactInformation, availableFields.get("accountContactName".toUpperCase()).getCategory());

		assertEquals("OK if close to expected because we added a few fields", 31, availableFields.size());
	}

}
