package com.picsauditing.PICS;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import sun.security.util.PendingException;

import com.picsauditing.EntityFactory;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorOperatorFlagDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.FlagChangesSubscription;
import com.picsauditing.mail.InsuranceCertificateSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionBuilder;
import com.picsauditing.mail.SubscriptionTimePeriod;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@Transactional
public class SubscriptionBuilderTest extends TestCase {

	@Autowired
	EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	ContractorOperatorFlagDAO flagDAO;
	@Autowired
	ContractorAuditOperatorDAO caoDAO;

	@Test
	public void testFlagChanges() throws Exception {
		SubscriptionBuilder builder = new FlagChangesSubscription(SubscriptionTimePeriod.Weekly, subscriptionDAO,
				flagDAO);
		
		builder.process();
	}

	@Test
	public void testInsuranceCerts() throws Exception {
		SubscriptionBuilder builder = new InsuranceCertificateSubscription(Subscription.VerifiedInsuranceCerts,
				SubscriptionTimePeriod.Weekly, subscriptionDAO, caoDAO);

		builder.process();

		builder = new InsuranceCertificateSubscription(Subscription.PendingInsuranceCerts,
				SubscriptionTimePeriod.Weekly, subscriptionDAO, caoDAO);

		builder.process();
	}

	@Test
	public void testIsSendEmail() {
		SubscriptionBuilder builder = new FlagChangesSubscription(SubscriptionTimePeriod.Daily, subscriptionDAO,
				flagDAO);

		Map<SubscriptionTimePeriod, EmailSubscription> timeMap = new HashMap<SubscriptionTimePeriod, EmailSubscription>();
		for (SubscriptionTimePeriod stp : SubscriptionTimePeriod.values()) {
			if (!stp.equals(SubscriptionTimePeriod.Event))
				timeMap.put(stp, EntityFactory.makeEmailSubscription(new User(2357), Subscription.FlagChanges, stp));
		}

		// Quarterly test - all should be True
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);

		for (Map.Entry<SubscriptionTimePeriod, EmailSubscription> entry : timeMap.entrySet()) {
			entry.getValue().setLastSent(cal.getTime());
			if (entry.getKey().equals(SubscriptionTimePeriod.None))
				assertFalse(entry.getKey() + " - failed for last quarter's date", builder.isSendEmail(entry.getValue()));
			else
				assertTrue(entry.getKey() + " - failed for last quarter's date", builder.isSendEmail(entry.getValue()));
		}

		// Monthly Test - all but Quarterly should be true
		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);

		for (Map.Entry<SubscriptionTimePeriod, EmailSubscription> entry : timeMap.entrySet()) {
			entry.getValue().setLastSent(cal.getTime());
			if (entry.getKey().equals(SubscriptionTimePeriod.None)
					|| entry.getKey().equals(SubscriptionTimePeriod.Quarterly))
				assertFalse(entry.getKey() + " - failed for last month's date", builder.isSendEmail(entry.getValue()));
			else
				assertTrue(entry.getKey() + " - failed for last month's date", builder.isSendEmail(entry.getValue()));
		}

		// Weekly Test - all but monthly and quarterly
		cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, -1);

		for (Map.Entry<SubscriptionTimePeriod, EmailSubscription> entry : timeMap.entrySet()) {
			entry.getValue().setLastSent(cal.getTime());
			if (entry.getKey().equals(SubscriptionTimePeriod.None)
					|| entry.getKey().equals(SubscriptionTimePeriod.Quarterly)
					|| entry.getKey().equals(SubscriptionTimePeriod.Monthly))
				assertFalse(entry.getKey() + " - failed for last week's date", builder.isSendEmail(entry.getValue()));
			else
				assertTrue(entry.getKey() + " - failed for last week's date", builder.isSendEmail(entry.getValue()));
		}

		// Daily Test - only daily should be true
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);

		for (Map.Entry<SubscriptionTimePeriod, EmailSubscription> entry : timeMap.entrySet()) {
			entry.getValue().setLastSent(cal.getTime());
			if (!entry.getKey().equals(SubscriptionTimePeriod.Daily))
				assertFalse(entry.getKey() + " - failed for yesterday's date", builder.isSendEmail(entry.getValue()));
			else
				assertTrue(entry.getKey() + " - failed for yesterday's date", builder.isSendEmail(entry.getValue()));
		}

		// All Should be false when given a date in the future
		cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);

		for (Map.Entry<SubscriptionTimePeriod, EmailSubscription> entry : timeMap.entrySet()) {
			entry.getValue().setLastSent(cal.getTime());
			assertFalse(entry.getKey() + " - failed for yesterday's date", builder.isSendEmail(entry.getValue()));
		}

	}

}
