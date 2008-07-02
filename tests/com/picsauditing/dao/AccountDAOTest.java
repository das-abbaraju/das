package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AccountDAOTest extends TestCase {

	@Autowired
	private AccountDAO accountdao;

	@Test
	public void testFind() {
		Account account = accountdao.find(3487);
		assertEquals("Trevor Allred", account.getName());
	}

	@Test
	public void testFindWhere() {
		List<Account> account = accountdao.findWhere("type LIKE 'Corporate'");
		assertTrue(account.size() > 9);
	}
}
