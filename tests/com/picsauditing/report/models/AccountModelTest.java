package com.picsauditing.report.models;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.report.Column;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.search.SelectSQL;

public class AccountModelTest extends ModelTest {
	private AccountModel model;

	@Before
	public void setup() {
		super.setup();
		model = new AccountModel(permissions);
	}

	@Test
	public void testAvailableFields() throws Exception {
		availableFields = model.getAvailableFields();

		assertTrue(availableFields.size() > 10);

		assertFalse("User.isActive is Low importance",
				availableFields.containsKey("accountContactIsActive".toUpperCase()));
		assertTrue("User.email is Average importance", availableFields.containsKey("accountContactEmail".toUpperCase()));
		assertTrue("User.name is Required", availableFields.containsKey("accountContactName".toUpperCase()));
		assertTrue("User.ID is Required", availableFields.containsKey("accountContactID".toUpperCase()));
		assertEquals(FieldCategory.ContactInformation, availableFields.get("accountContactName".toUpperCase())
				.getCategory());

		assertEquals("OK if close to expected because we added a few fields", 30, availableFields.size());
	}

	@Test
	public void testSql() throws Exception {
		definition.getColumns().add(new Column("accountCountry"));
		SelectSQL sql = new SqlBuilder().initializeSql(model, definition, permissions);
		String sqlResult = sql.toString();
		assertContains("SELECT Account.country AS `accountCountry` FROM accounts AS Account", sqlResult);
		// TODO check that it doesn't add the left joins to NAICS
	}
}
