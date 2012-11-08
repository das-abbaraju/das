package com.picsauditing.report.tables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AccountTableTest {

	@Test
	public void testAccountFields() {
		AccountTable accountTable = new AccountTable();
		assertEquals("TRIM({TO_ALIAS}.name)", accountTable
				.getField("Name").getDatabaseColumnName());
		assertEquals("Name", accountTable.getField("Name").getName());
		assertTrue(accountTable.getFields().size() > 10);
	}

	@Test
	public void testAccountJoins() {
		AccountTable accountTable = new AccountTable();
		assertEquals(FieldImportance.Average, accountTable.getKey(AccountTable.Contact).getMinimumImportance());
	}
}