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
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmailSubscriptionDAOTest {

	@Autowired
	private EmailSubscriptionDAO dao;

	@Test
	public void testFindBySubscription() {
		EmailSubscription sub = EntityFactory.makeEmailSubscription(new User(2357), Subscription.FinishPICSProcess,
				SubscriptionTimePeriod.Daily);

		sub = dao.save(sub);

		List<EmailSubscription> subscriptionList = dao.find(Subscription.FinishPICSProcess,
				SubscriptionTimePeriod.Daily);
		assertTrue(subscriptionList.contains(sub));
		assertEquals(2357, subscriptionList.get(0).getUser().getId());

		dao.remove(sub.getId());
		assertNull(dao.find(sub.getId()));
	}
}
