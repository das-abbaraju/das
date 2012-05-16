package com.picsauditing.report.tables;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccountTableTest {

	@Test
	public void testAccountFields() {
		AccountTable accountTable = new AccountTable();
		assertEquals("a.name", accountTable.getAvailableFieldsMap().get("ACCOUNTNAME").getDatabaseColumnName());
		assertEquals("accountName", accountTable.getAvailableFieldsMap().get("ACCOUNTNAME").getName());
		assertTrue(accountTable.getAvailableFieldsMap().size() > 10);
		System.out.println(accountTable.getAvailableFieldsMap().size());
		
		ContractorTable contractorTable = new ContractorTable("account", "a");
		for (String fieldName : contractorTable.getAvailableFieldsMap().keySet()) {
			System.out.println(fieldName + ": " + contractorTable.getAvailableFieldsMap().get(fieldName));
		}
	}

}
