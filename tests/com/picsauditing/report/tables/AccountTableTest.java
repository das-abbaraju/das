package com.picsauditing.report.tables;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccountTableTest {

	@Test
	public void testAccountFields() {
		AccountTable accountTable = new AccountTable();
		assertEquals("a.name", accountTable.getAvailableFields().get("ACCOUNTNAME").getDatabaseColumnName());
		assertEquals("accountName", accountTable.getAvailableFields().get("ACCOUNTNAME").getName());
		assertTrue(accountTable.getAvailableFields().size() > 10);
//		System.out.println(accountTable.getAvailableFields().size());
		
		ContractorTable contractorTable = new ContractorTable("account", "a");
		for (String fieldName : contractorTable.getAvailableFields().keySet()) {
//			System.out.println(fieldName + ": " + contractorTable.getAvailableFields().get(fieldName));
		}
	}
}
