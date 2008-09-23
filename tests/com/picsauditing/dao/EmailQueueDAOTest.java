package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.EmailQueue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class EmailQueueDAOTest {

	@Autowired
	private EmailQueueDAO emailQueueDAO;

	@Test
	public void testSaveAndRemove() {
		EmailQueue emailQueue = new EmailQueue();
		emailQueue.setStatus("Pending");
		emailQueue.setFromAddress("tester@picsauditing.com");
		emailQueue.setToAddresses("tester@picsauditing.com");
		emailQueue.setSubject("Test Email");
		emailQueue.setPriority(10);
		emailQueue.setCreationDate(new Date());

		emailQueueDAO.save(emailQueue);
		assertEquals(true, emailQueue.getId() > 0);
		emailQueueDAO.remove(emailQueue.getId());
		assertNull(emailQueueDAO.find(emailQueue.getId()));
	}
}
