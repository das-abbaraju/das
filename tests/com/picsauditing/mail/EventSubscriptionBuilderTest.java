package com.picsauditing.mail;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class EventSubscriptionBuilderTest extends TestCase {

	@Autowired
	EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	ContractorOperatorDAO coDAO;
	@Autowired
	EmailQueueDAO qDAO;
	@Autowired
	ContractorAccountDAO conDAO;

	@Test
	public void testContractorFinishedSubscription() throws Exception {
		ContractorOperator co = coDAO.find(3, 4744);

		EventSubscriptionBuilder.contractorFinishedEvent(subscriptionDAO, co);

		List<EmailQueue> emails = qDAO.getPendingEmails("emailTemplate.id = 63", 10);

		assertTrue("The email should be waiting in the queue", emails.size() > 0);

		boolean justSent = false;
		for (EmailQueue q : emails) {
			if (DateBean.getDateDifference(q.getCreationDate()) == 0) {
				assertTrue(q.getSubject().contains(co.getContractorAccount().getName()));
				justSent = true;
			}
		}

		assertTrue("The email should have the same creationDate as today", justSent);
	}

	@Test
	public void testContractorInvoiceEvent() throws Exception {
		ContractorAccount con = conDAO.find(3);
		
		EventSubscriptionBuilder.contractorInvoiceEvent(con, con.getInvoices().get(0), null);
	}
}