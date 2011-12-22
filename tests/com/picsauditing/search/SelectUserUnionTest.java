package com.picsauditing.search;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SelectUserUnionTest {
	SelectUserUnion sql;

	@Before
	public void setUp() throws Exception {
		sql = new SelectUserUnion();
	}
	@Test
	public void testSQL_blank() {
		assertEquals("SELECT *\nFROM (\nSELECT id, username, password, email, name, isActive, creationDate, lastLogin, accountID, phoneIndex as phone FROM users where isGroup ='No' \n) u",sql.toString());
	}
	@Test
	public void testSQL_additional() {
		sql.addField("u.id");
		sql.addField("u.username");
		sql.addField("u.name");
		sql.addField("u.accountID");
		sql.addField("a.name");
		sql.addJoin("JOIN accounts a ON a.id = u.accountID");

		sql.addWhere("u.isActive = 'Yes'");
		sql.addOrderBy("u.name");
		sql.setLimit(100);
		assertEquals("SELECT u.id, u.username, u.name, u.accountID, a.name\nFROM (\nSELECT id, username, password, email, name, isActive, creationDate, lastLogin, accountID, phoneIndex as phone FROM users where isGroup ='No' \n) u\nJOIN accounts a ON a.id = u.accountID\nWHERE 1\n AND (u.isActive = 'Yes') \nORDER BY u.name\nLIMIT 100",sql.toString());
	}


}
