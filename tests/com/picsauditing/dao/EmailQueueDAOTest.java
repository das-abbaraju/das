package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.EmailQueue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@Transactional
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
		assertEquals(true, emailQueue.getEmailID() > 0);
		EmailQueue emailQueue2 = emailQueueDAO.find(emailQueue.getEmailID());
		emailQueueDAO.remove(emailQueue2);
		assertNull(emailQueueDAO.find(emailQueue2.getEmailID()));
	}
}
