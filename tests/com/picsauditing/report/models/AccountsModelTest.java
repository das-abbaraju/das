package com.picsauditing.report.models;

import static com.picsauditing.util.Assert.assertContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.jpa.entities.Column;
import com.picsauditing.report.SqlBuilder;
import com.picsauditing.report.tables.FieldCategory;
import com.picsauditing.search.SelectSQL;

public class AccountsModelTest extends ModelTest {
	private AccountsModel model;

	@Before
	public void setup() {
		super.setup();
		model = new AccountsModel(permissions);
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
}
