package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.Account;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AccountDAOTest extends TestCase {

	@Autowired
	private AccountDAO accountdao;

	@Test
	public void testFind() {
		Account account = accountdao.find(3);
		assertEquals("Ancon Marine", account.getName());
	}

	@Test
	public void testFindWhere() {
		List<Account> account = accountdao.findWhere("type LIKE 'Corporate'");
		assertTrue(account.size() > 9);
	}

	@Test
	public void testAccountEmployees() throws Exception {
		Account account = accountdao.find(1100);

		assertTrue(account.getEmployees().size() > 0);
	}
}
