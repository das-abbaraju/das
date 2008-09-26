package com.picsauditing.dao;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class EmailQueueDAOTest extends TestCase {

	@Autowired
	private EmailQueueDAO emailQueueDAO;

	@Test
	public void testSaveAndRemove() {
		EmailQueue emailQueue = new EmailQueue();
		emailQueue.setFromAddress("junit@picsauditing.com");
		emailQueue.setToAddresses("junit@picsauditing.com");
		emailQueue.setSubject("Test Email");
		emailQueue.setPriority(10);
		emailQueue.setCreationDate(new Date());

		emailQueueDAO.save(emailQueue);
		assertTrue(emailQueue.getId() > 0);
		assertTrue(emailQueue.getStatus().equals(EmailStatus.Pending));
		emailQueueDAO.remove(emailQueue.getId());
		assertNull(emailQueueDAO.find(emailQueue.getId()));
	}
}
