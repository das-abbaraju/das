package com.picsauditing.report.tables;

import static org.junit.Assert.*;

import org.junit.Test;

public class AccountTest {

	@Test
	public void testAccountFields() {
		Account account = new Account();
		assertEquals("a.name", account.getFields().get("ACCOUNTNAME").getSql());
		assertEquals("accountName", account.getFields().get("ACCOUNTNAME").getName());
		assertTrue(account.getFields().size() > 10);
		System.out.println(account.getFields().size());
		
		Contractor contractor = new Contractor("a");
		for (String fieldName : contractor.getFields().keySet()) {
			System.out.println(fieldName + ": " + contractor.getFields().get(fieldName));
		}
	}

}
