package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmailSubscriptionDAOTest {

	@Autowired
	private EmailSubscriptionDAO dao;
	@Autowired
	private UserDAO userDAO;

	@Test
	public void testFindBySubscription() {
		EmailSubscription sub = EntityFactory.makeEmailSubscription(userDAO.find(2357), Subscription.FinishPICSProcess,
				SubscriptionTimePeriod.Daily);

		sub = dao.save(sub);

		List<EmailSubscription> subscriptionList = dao.find(Subscription.FinishPICSProcess,
				SubscriptionTimePeriod.Daily);
		assertTrue(subscriptionList.contains(sub));
		assertEquals(2357, subscriptionList.get(0).getUser().getId());

		dao.remove(sub.getId());
		assertNull(dao.find(sub.getId()));
	}

	@Test
	public void testOnlySendToActive() {
		EmailSubscription sub = EntityFactory.makeEmailSubscription(userDAO.find(9657), Subscription.FinishPICSProcess,
				SubscriptionTimePeriod.Daily);

		sub = dao.save(sub);

		List<EmailSubscription> subscriptionList = dao.find(Subscription.FinishPICSProcess,
				SubscriptionTimePeriod.Daily);

		assertTrue(subscriptionList.size() == 0);

		dao.remove(sub.getId());
		assertNull(dao.find(sub.getId()));
	}

	@Test
	public void testOnlySendToActiveOperator() {
		EmailSubscription sub = EntityFactory.makeEmailSubscription(userDAO.find(9657), Subscription.FinishPICSProcess,
				SubscriptionTimePeriod.Daily);

		sub = dao.save(sub);

		List<EmailSubscription> subscriptionList = dao.find(Subscription.FinishPICSProcess, 7727);

		assertTrue(subscriptionList.size() == 0);

		subscriptionList = dao.find(Subscription.FinishPICSProcess, SubscriptionTimePeriod.Daily, 7727);

		assertTrue(subscriptionList.size() == 0);

		dao.remove(sub.getId());
		assertNull(dao.find(sub.getId()));
	}
}
