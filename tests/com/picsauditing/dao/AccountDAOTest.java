package com.picsauditing.dao;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AccountDAOTest extends TestCase {

	@Autowired
	private AccountDAO accountdao;
	private int accountID = 0;

	@Test
	public void testSaveAndRemove() {
		Account account = new ContractorAccount();
		account.setName("PICS");
		account.setUsername("testpics112");
		account.setPassword("testpics");
		account.setPasswordChange(new Date(12 - 12 - 2008));
		account.setLastLogin(Calendar.getInstance().getTime());
		account.setContact("pics admin");
		account.setAddress("17701 cowan");
		account.setCity("irvine");
		account.setState("ca");
		account.setZip("92345");
		account.setPhone("999-999-9999");
		account.setPhone2("999-999-9999");
		account.setFax("999-999-9999");
		account.setEmail("pics@picsauditing.com");
		account.setWebUrl("www.picsauditing.com");
		account.setIndustry("contracting");
		account.setActive('y');
		account.setCreatedBy("pics");
		account.setDateCreated(new Date(2008 - 04 - 04));
		account.setSeesAllB('n');
		account.setActivationEmailsB("pics@picsauditing.com");
		account.setSendActivationEmailB('n');
		account.setEmailConfirmedDate(new Date(2008 - 12 - 42));
		account = accountdao.save(account);
		assertEquals("testpics112", account.getUsername());
		assertTrue(account.getId() > 0);
		accountID = account.getId();
		
		accountdao.remove(accountID);
		Account account1 = accountdao.find(this.accountID);
		assertNull(account1);
	
	}


	@Test
	public void testFind() {
		Account account = accountdao.find(43);
		assertEquals("90810", account.getZip());
	}

	@Test
	public void testFindWhere() {
		List<Account> account = accountdao.findWhere("city LIKE 'Corona'");
		assertTrue(account.size() > 9);
	}

	@Test
	public void testFindOperators() {
		List<Account> account = accountdao.findOperators();
		assertEquals("238705843", account.get(0).getUsername());
	}

}
