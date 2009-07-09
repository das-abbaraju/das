package com.picsauditing.PICS;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.mail.ContractorRegistrationSubscription;
import com.picsauditing.mail.FlagChangesSubscription;
import com.picsauditing.mail.InsuranceCertificateSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionBuilder;
import com.picsauditing.mail.SubscriptionTimePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class SubscriptionBuilderTest extends TestCase {

	@Autowired
	EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	ContractorOperatorFlagDAO flagDAO;
	@Autowired
	ContractorAuditOperatorDAO caoDAO;
	@Autowired
	OperatorAccountDAO opDAO;
	@Autowired
	ContractorAccountDAO conDAO;

	@Test
	public void testFlagChanges() throws Exception {
		SubscriptionBuilder builder = new FlagChangesSubscription(SubscriptionTimePeriod.Weekly, subscriptionDAO,
				flagDAO);

		builder.process();
	}

	@Test
	public void testVerifiedInsuranceCerts() throws Exception {
		SubscriptionBuilder builder = new InsuranceCertificateSubscription(Subscription.VerifiedInsuranceCerts,
				SubscriptionTimePeriod.Weekly, subscriptionDAO, caoDAO, opDAO);

		builder.process();
	}

	@Test
	public void testPendingInsuranceCerts() throws Exception {
		SubscriptionBuilder builder = new InsuranceCertificateSubscription(Subscription.PendingInsuranceCerts,
				SubscriptionTimePeriod.Weekly, subscriptionDAO, caoDAO, opDAO);

		builder.process();
	}

	@Test
	public void testContractorRegistration() throws Exception {
		SubscriptionBuilder builder = new ContractorRegistrationSubscription(SubscriptionTimePeriod.Weekly,
				subscriptionDAO, conDAO);

		builder.process();
	}
}
