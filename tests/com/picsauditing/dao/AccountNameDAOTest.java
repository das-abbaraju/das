package com.picsauditing.dao;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AccountNameDAOTest extends TestCase {

	@Autowired
	private AccountNameDAO dao;

	@Test
	public void testCRUD() {
		OperatorAccount operator = new OperatorAccount();
		operator.setId(16); // BP Carson
		
		AccountName accountName = new AccountName();
		accountName.setName("BP West Coast Incorporated");
		accountName.setAccount(operator);
		accountName.setAuditColumns(new User(941));
		
		accountName = dao.save(accountName);
		assertTrue(accountName.getId() > 0);
		
		dao.remove(accountName.getId());
	}
}
