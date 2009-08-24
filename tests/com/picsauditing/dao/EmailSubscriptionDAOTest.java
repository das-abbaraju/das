package com.picsauditing.dao;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EmailSubscriptionDAOTest {

	@Autowired
	private EmailSubscriptionDAO dao;

//	@Test
//	public void testFindBySubscription() {
//		EmailSubscription sub = EntityFactory.makeEmailSubscription(new User(2357), Subscription.PICSAnnouncements,
//				SubscriptionTimePeriod.Daily);
//
//		sub = dao.save(sub);
//
//		List<EmailSubscription> subscriptionList = dao.find(Subscription.PICSAnnouncements,
//				SubscriptionTimePeriod.Daily);
//		assertTrue(subscriptionList.contains(sub));
//
//		dao.remove(sub.getId());
//		assertNull(dao.find(sub.getId()));
//	}
}
