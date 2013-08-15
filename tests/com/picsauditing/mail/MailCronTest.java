package com.picsauditing.mail;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.actions.report.ReportApi;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.mail.subscription.ContractorAddedSubscription;
import com.picsauditing.mail.subscription.DynamicReportsSubscription;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.toggle.FeatureToggle;

public class MailCronTest extends PicsTranslationTest {
	private MailCron mailCron;
	private int subscriptionID = 123;
	private List<EmailQueue> emails;

	@Mock
	private SubscriptionBuilderFactory subscriptionFactory;
	@Mock
	private SubscriptionBuilder builder;
	@Mock
	private EmailSubscription emailSubscription;
	@Mock
	private Report report;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailQueue email;
	@Mock
	private FeatureToggle featureToggleChecker;
	@Mock
	private AppPropertyDAO appPropDAO;
	@Mock
	private EmailSubscriptionDAO subscriptionDAO;
	@Mock
	private EmailQueueDAO emailQueueDAO;
	@Mock
	private AppProperty enableSubscriptions;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();

		mailCron = new MailCron();

		PicsTestUtil.autowireDAOsFromDeclaredMocks(mailCron, this);
		emails = new ArrayList<EmailQueue>();
		emails.add(email);

		mailCron.setSubscriptionID(subscriptionID);
		when(enableSubscriptions.getValue()).thenReturn("true");
		when(appPropDAO.find("subscription.enable")).thenReturn(enableSubscriptions);
		when(subscriptionFactory.getBuilder((Subscription) any())).thenReturn(builder);
		when(subscriptionDAO.find(subscriptionID)).thenReturn(emailSubscription);
		when(emailSubscription.getReport()).thenReturn(report);
		when(emailSubscription.getSubscription()).thenReturn(Subscription.AmberFlags);
		when(emailQueueDAO.getPendingEmails(1)).thenReturn(emails);
		Whitebox.setInternalState(mailCron, "subscriptionFactory", subscriptionFactory);
		Whitebox.setInternalState(mailCron, "emailSender", emailSender);
		Whitebox.setInternalState(mailCron, "featureToggleChecker", featureToggleChecker);
		// TODO: figure out why this isn't getting set in
		// autowireDAOsFromDeclaredMocks
		Whitebox.setInternalState(mailCron, "appPropDAO", appPropDAO);
	}

	@Test
	public void testExecute_SubscriptionDisnabledQueueDisabledDoesBothSubsandQueue() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(false);
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(false);

		mailCron.execute();

		verify(builder).sendSubscription(emailSubscription);
		verify(emailSender).sendNow(email);
	}

	@Test
	public void testExecute_SubscriptionEnabledQueueEnabledDoNothing() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(true);
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(true);

		mailCron.execute();

		verifyZeroInteractions(builder, emailSender);
	}

	@Test
	// PICS-12304
	public void testExecute_EmailSubscriptionForDynamicReportsWithNullReportShouldThrowValidationError() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(false);
		EmailSubscription invalidEmailSubscription = createEmailSubscription(Subscription.DynamicReports, null);
		when(subscriptionDAO.find(subscriptionID)).thenReturn(invalidEmailSubscription);
		SubscriptionBuilder subscriptionBuilder = createDynamicReportsSubscription();
		when(subscriptionFactory.getBuilder((Subscription) any())).thenReturn(subscriptionBuilder);

		mailCron.execute();

		assertTrue(mailCron.hasActionErrors());
		assert(mailCron.getActionErrors().contains(MailCron.ERROR_INVALID_SUBSCRIPTION));
	}

	@Test
	public void testExecute_EmailSubscriptionWithNullSubscriptionShouldThrowValidationError() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(false);
		EmailSubscription invalidEmailSubscription = createEmailSubscription(null, new Report());
		when(subscriptionDAO.find(subscriptionID)).thenReturn(invalidEmailSubscription);
		SubscriptionBuilder subscriptionBuilder = createContractorAddedSubscription();
		when(subscriptionFactory.getBuilder((Subscription) any())).thenReturn(subscriptionBuilder);

		mailCron.execute();

		assertTrue(mailCron.hasActionErrors());
		assert(mailCron.getActionErrors().contains(MailCron.ERROR_INVALID_SUBSCRIPTION));
	}

	private EmailSubscription createEmailSubscription(Subscription subscription, Report report) {
		EmailSubscription emailSubscription = new EmailSubscription();
		emailSubscription.setSubscription(subscription);
		emailSubscription.setReport(report);
		return emailSubscription;
	}

	private DynamicReportsSubscription createDynamicReportsSubscription() {
		DynamicReportsSubscription dynamicReportsSubscription = new DynamicReportsSubscription();
		ReportApi reportApi = new ReportApi();
		Whitebox.setInternalState(dynamicReportsSubscription, "reportApi", reportApi);
		return dynamicReportsSubscription;
	}

	private ContractorAddedSubscription createContractorAddedSubscription() {
		ContractorAddedSubscription contractorAddedSubscription = new ContractorAddedSubscription();
		return contractorAddedSubscription;
	}
}
