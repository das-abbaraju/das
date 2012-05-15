package com.picsauditing.report.tables;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccountTest {

	@Test
	public void testAccountFields() {
		Account account = new Account();
		assertEquals("a.name", account.getAvailableFieldsMap().get("ACCOUNTNAME").getSql());
		assertEquals("accountName", account.getAvailableFieldsMap().get("ACCOUNTNAME").getName());
		assertTrue(account.getAvailableFieldsMap().size() > 10);
		System.out.println(account.getAvailableFieldsMap().size());
		
		Contractor contractor = new Contractor("account", "a");
		for (String fieldName : contractor.getAvailableFieldsMap().keySet()) {
			System.out.println(fieldName + ": " + contractor.getAvailableFieldsMap().get(fieldName));
		}
	}

}
