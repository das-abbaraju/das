package com.picsauditing.dao;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class UserDaoTest extends TestCase {

	@Autowired
	protected UserDAO dao;

	@Test
	public final void testFind() {
		try {
			User row = dao.find(681);
			OperatorAccount operator = (OperatorAccount) row.getAccount();
			assertEquals(2921, row.getAccount().getId());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public final void testFindAll() {
		try {
			List<User> rows = dao.findWhere("username LIKE 'james%'");
			assertTrue(rows.size() > 5);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindAuditors() {
		List<User> userlist = dao.findAuditors();
		System.out.println(userlist.get(0).getName());
	}

	@Test
	public void testFindName() {
		User user = dao.findName("picstest");
		assertEquals(65, user.getId());
	}

	@Test
	public void testUsersCounts() {
		int count = dao.getUsersCounts();
		System.out.println(count);
	}

	@Test
	public void testRecentLoggedOperators() {
		List<User> users = dao.findRecentLoggedOperators();
		System.out.println(users.get(0).getName());
	}

	@Test
	public void testFindByAccountID() {
		List<User> user = dao.findByAccountID(1100, "Yes", "No");
		assertTrue(user.size() > 0);
	}

	@Test
	public void testEmailSubscriptions() {
		User user = dao.find(1282);

		assertEquals(user.getAccount().getId(), 4744);

		assertTrue(user.getSubscriptions().size() > 0);

		boolean hasConFinishedEvent = false;
		for (EmailSubscription subscription : user.getSubscriptions()) {
			if (subscription.getSubscription().equals(Subscription.ContractorFinished)
					&& subscription.getTimePeriod().equals(SubscriptionTimePeriod.Event))
				hasConFinishedEvent = true;
		}

		assertTrue(hasConFinishedEvent);
	}
}
