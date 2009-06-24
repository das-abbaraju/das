package com.picsauditing.dao;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.EntityFactory;
import com.picsauditing.email.Subscription;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class EmailSubscriptionDAOTest {

	@Autowired
	private EmailSubscriptionDAO dao;

	@Test
	public void testFindBySubscription() {
		EmailSubscription sub = EntityFactory.makeEmailSubscription(new User(2357), Subscription.PICSAnnouncements);

		sub = dao.save(sub);

		List<EmailSubscription> subscriptionList = dao.findBySubscription(Subscription.PICSAnnouncements);
		assertTrue(subscriptionList.contains(sub));

		dao.remove(sub.getId());

		assertNull(dao.find(sub.getId()));
	}
}
