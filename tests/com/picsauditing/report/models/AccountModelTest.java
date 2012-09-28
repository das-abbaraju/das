package com.picsauditing.report.models;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

		excludedFields.add("AccountContactLastLogin");

		includedFields.add("AccountContactEmail");
		includedFields.add("AccountContactName");
		includedFields.add("AccountContactID");
		assertEquals(FieldCategory.ContactInformation, availableFields.get("accountContactName".toUpperCase())
				.getCategory());
		
		includedFields.add("AccountContactID");
		
		checkFields();
	}

	@Test
	public void testSql() throws Exception {
		definition.getColumns().add(new Column("accountCountry"));
		SelectSQL sql = new SqlBuilder().initializeSql(model, definition, permissions);
		String sqlResult = sql.toString();
		assertContains("SELECT Account.country AS `accountCountry` FROM accounts AS Account", sqlResult);
		assertFalse(sqlResult.contains("NAICS"));
	}
}
