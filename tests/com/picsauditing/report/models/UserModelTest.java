package com.picsauditing.report.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.fields.Field;

public class UserModelTest extends ModelTest {

	private UserModel model;

	@Before
	public void setUp() {
		super.setUp();

		model = new UserModel(permissions);
	}

	@Test
	public void testUserModel_FieldNamesShouldNotChange() {
		// If you change these values, you will break reports in the database because we
		// store the field name and use it to lookup an available field.
		// If you must change these values, change the entities in the database as well.
		assertEquals("AccountType", UserModel.ACCOUNT_TYPE);
	}

	@Test
	public void testUserModel_FieldIndexedByExpectedKey() {
		Map<String, Field> fields = model.getAvailableFields();

		Field field = fields.get("AccountType".toUpperCase());
		assertNotNull(field);
		assertEquals("AccountType", field.getName());
	}
}
