package com.picsauditing.report.tables;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AccountTableTest {

	@Test
	public void testAccountFields() {
		AccountTable accountTable = new AccountTable();
		assertEquals("CASE WHEN TRIM({TO_ALIAS}.dbaName) IS NULL THEN TRIM({TO_ALIAS}.name) "
				+ "WHEN TRIM({TO_ALIAS}.dbaName) = '' THEN TRIM({TO_ALIAS}.name) ELSE TRIM({TO_ALIAS}.dbaName) END", accountTable
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
