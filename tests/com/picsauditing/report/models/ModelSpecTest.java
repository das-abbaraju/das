package com.picsauditing.report.models;

import org.junit.Test;

import com.picsauditing.report.tables.AccountTable;
import com.picsauditing.report.tables.FieldCategory;


public class ModelSpecTest {

	@Test
	public void testBasic() throws Exception {
		ModelSpec account = new ModelSpec(null, "Account");
		account.join(AccountTable.Contact).category = FieldCategory.ContactInformation;
		account.join(AccountTable.Naics);
	}
}
