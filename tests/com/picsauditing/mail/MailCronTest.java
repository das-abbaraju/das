package com.picsauditing.mail;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.search.Database;
import com.picsauditing.toggle.FeatureToggleChecker;


public class MailCronTest {
	private MailCron mailCron;
	private int subscriptionID = 123;
	private List<EmailQueue> emails;
	
	@Mock private SubscriptionBuilderFactory subscriptionFactory;
	@Mock private SubscriptionBuilder builder;
	@Mock private EmailSubscription emailSubscription;
	@Mock private EmailSender emailSender;
	@Mock private EmailQueue email;
	@Mock private FeatureToggleChecker featureToggleChecker;
	@Mock private AppPropertyDAO appPropDAO;
	@Mock private EmailSubscriptionDAO subscriptionDAO;
	@Mock private EmailQueueDAO emailQueueDAO;
	@Mock private Database databaseForTesting;
	@Mock private AppProperty enableSubscriptions;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		mailCron = new MailCron();

		PicsTestUtil.autowireDAOsFromDeclaredMocks(mailCron, this);
		emails = new ArrayList<EmailQueue>();
		emails.add(email);
		
		mailCron.setSubscriptionID(subscriptionID);
		when(enableSubscriptions.getValue()).thenReturn("true");
		when(appPropDAO.find("subscription.enable")).thenReturn(enableSubscriptions);
		when(subscriptionFactory.getBuilder((Subscription)any())).thenReturn(builder);
		when(subscriptionDAO.find(subscriptionID)).thenReturn(emailSubscription);
		when(emailQueueDAO.getPendingEmails(1)).thenReturn(emails);
		Whitebox.setInternalState(mailCron, "subscriptionFactory", subscriptionFactory);
		Whitebox.setInternalState(mailCron, "emailSender", emailSender);
		Whitebox.setInternalState(mailCron, "featureToggleChecker", featureToggleChecker);
		// TODO: figure out why this isn't getting set in autowireDAOsFromDeclaredMocks
		Whitebox.setInternalState(mailCron, "appPropDAO", appPropDAO);
	}
	
	@Test
	public void testExecute_SubscriptionDisnabledQueueDisabledDoesBothSubsandQueue() throws Exception {
		when(featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.SubscriptionEmail")).thenReturn(false);
		when(featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.EmailQueue")).thenReturn(false);
		
		mailCron.execute();
		
		verify(builder).sendSubscription(emailSubscription);
		verify(emailSender).sendNow(email);
	}
	
	@Test
	public void testExecute_SubscriptionEnabledQueueEnabledDoNothing() throws Exception {
		when(featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.SubscriptionEmail")).thenReturn(true);
		when(featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.EmailQueue")).thenReturn(true);
		
		mailCron.execute();
		
		verifyZeroInteractions(builder, emailSender);
	}
}
