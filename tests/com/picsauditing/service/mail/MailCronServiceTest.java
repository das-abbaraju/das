package com.picsauditing.service.mail;

import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ReportApi;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Report;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.subscription.ContractorAddedSubscription;
import com.picsauditing.mail.subscription.DynamicReportsSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.validator.ValidationException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MailCronServiceTest {

	private MailCronService mailCronService;
	private int subscriptionId = 123;
	private List<EmailQueue> emails;
	private EmailQueue email;

	@Mock
	private FeatureToggleCheckerGroovy featureToggleChecker;
	@Mock
	private AppPropertyService appPropertyService;
	@Mock
	private Permissions permissions;
	@Mock
	private SubscriptionBuilderFactory subscriptionFactory;
	@Mock
	private SubscriptionBuilder builder;
	@Mock
	private EmailSubscription emailSubscription;
	@Mock
	private Report report;
	@Mock
	private EmailSubscriptionDAO subscriptionDAO;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailQueueDAO emailQueueDAO;

	@Before
	public void setUp() throws Exception {
		mailCronService = new MailCronService();

		MockitoAnnotations.initMocks(this);
		emails = new ArrayList<>();
		email = new EmailQueue();
		emails.add(email);

		when(subscriptionFactory.getBuilder((Subscription) any())).thenReturn(builder);
		when(subscriptionDAO.find(subscriptionId)).thenReturn(emailSubscription);
		when(emailSubscription.getReport()).thenReturn(report);
		when(emailSubscription.getSubscription()).thenReturn(Subscription.AmberFlags);
		when(emailQueueDAO.getPendingEmails(1)).thenReturn(emails);

		Whitebox.setInternalState(mailCronService, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(mailCronService, "appPropertyService", appPropertyService);
		Whitebox.setInternalState(mailCronService, "subscriptionFactory", subscriptionFactory);
		Whitebox.setInternalState(mailCronService, "emailSender", emailSender);
		Whitebox.setInternalState(mailCronService, "subscriptionDAO", subscriptionDAO);
		Whitebox.setInternalState(mailCronService, "emailQueueDAO", emailQueueDAO);
	}

	@Test
	public void testProcessEmailSubscription_whenTOGGLE_BPROC_SUBSCRIPTIONEMAILisFalseAndSubscriptionsAreEnabled_sendSubscription() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(false);
		when(appPropertyService.emailSubscriptionsAreEnabled()).thenReturn(true);

		mailCronService.processEmailSubscription(subscriptionId, permissions);

		verify(builder).sendSubscription(emailSubscription);
	}

	@Test
	public void testProcessEmailSubscription_whenTOGGLE_BPROC_SUBSCRIPTIONEMAILisTrueAndSubscriptionsAreEnabled_doNothing() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(true);
		when(appPropertyService.emailSubscriptionsAreEnabled()).thenReturn(true);

		mailCronService.processEmailSubscription(subscriptionId, permissions);

		verify(builder, never()).sendSubscription(emailSubscription);
	}

	@Test
	public void testProcessEmailSubscription_whenTOGGLE_BPROC_SUBSCRIPTIONEMAILisFalseAndSubscriptionsAreNotEnabled_doNothing() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(true);
		when(appPropertyService.emailSubscriptionsAreEnabled()).thenReturn(false);

		mailCronService.processEmailSubscription(subscriptionId, permissions);

		verify(builder, never()).sendSubscription(emailSubscription);
	}

	@Test
	public void testProcessEmailSubscription_whenIOException_setSubscriptionToBeReprocessedTomorrow() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(false);
		when(appPropertyService.emailSubscriptionsAreEnabled()).thenReturn(true);
		doThrow(new IOException()).when(builder).sendSubscription(emailSubscription);

		mailCronService.processEmailSubscription(subscriptionId, permissions);

		verify(emailSubscription).setLastSent(any(Date.class));
		verify(subscriptionDAO).save(emailSubscription);
	}

	@Test(expected = ValidationException.class)
	public void testValidateEmailSubscription_nullEmailSubscriptionShouldThrowValidationError() throws Exception {
		EmailSubscription invalidEmailSubscription = null;

		mailCronService.validateEmailSubscription(invalidEmailSubscription);
	}

	@Test(expected = ValidationException.class)
	public void testValidateEmailSubscription_emailSubscriptionWithNullSubscriptionShouldThrowValidationError() throws Exception {
		EmailSubscription invalidEmailSubscription = createEmailSubscription(null, new Report());

		mailCronService.validateEmailSubscription(invalidEmailSubscription);
	}

	@Test(expected = ValidationException.class)
	public void testValidateEmailSubscription_emailSubscriptionForDynamicReportsWithNullReportShouldThrowValidationError() throws Exception {
		EmailSubscription invalidEmailSubscription = createEmailSubscription(Subscription.DynamicReports, null);

		mailCronService.validateEmailSubscription(invalidEmailSubscription);
	}

	@Test
	public void testFindEmailSubscription() throws Exception {

		mailCronService.findEmailSubscription(subscriptionId);

		verify(subscriptionDAO).find(subscriptionId);
	}

	@Test
	public void testProcessPendingEmails_whenTOGGLE_BPROC_EMAILQUEUEisFalse_sendEmail() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(false);

		String statusMessage = mailCronService.processPendingEmails(permissions);

		verify(emailSender).sendNow(email);
		assertTrue(statusMessage.equals(String.format(MailCronService.SUCCESSFULLY_SENT_EMAILS, 1, 1)));
	}

	@Test
	public void testProcessPendingEmails_whenSendErrorOccurs_processEmailForSendError() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(false);
		doThrow(MessagingException.class).when(emailSender).sendNow(email);

		String statusMessage = mailCronService.processPendingEmails(permissions);

		verify(emailSender).sendNow(email);
		assertEquals(EmailStatus.Error, email.getStatus());
		assertEquals(EmailAddressUtils.PICS_ERROR_EMAIL_ADDRESS, email.getToAddresses());
		verify(emailSender).send(email);
		assertTrue(statusMessage.equals(String.format(MailCronService.SUCCESSFULLY_SENT_EMAILS, 0, 1)));
	}

	@Test
	public void testProcessPendingEmails_whenTOGGLE_BPROC_EMAILQUEUEisFalseAndQueueIsEmpty_returnQueueEmptyMessage() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(false);
		when(emailQueueDAO.getPendingEmails(1)).thenReturn(Collections.EMPTY_LIST);

		String statusMessage = mailCronService.processPendingEmails(permissions);

		verify(emailSender, never()).sendNow(email);
		assertTrue(statusMessage.equals(MailCronService.THE_EMAIL_QUEUE_IS_EMPTY));
	}

	@Test
	public void testProcessPendingEmails_whenTOGGLE_BPROC_EMAILQUEUEisTrue_doNothing() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(true);

		mailCronService.processPendingEmails(permissions);

		verify(emailSender, never()).sendNow(email);
	}

	@Test
	public void testGetSubscriptionIdsToSendAsCommaDelimited() throws Exception {
		Integer[] ids = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		List<Integer> subscriptionIds = Arrays.asList(ids);
		when(subscriptionDAO.findSubscriptionsToSend(MailCronService.SUBSCRIPTIONS_TO_SEND)).thenReturn(subscriptionIds);

		String result = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();

		assertEquals("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", result);
	}

	@Test
	public void testGetSubscriptionIdsToSendAsCommaDelimited_emptyList() throws Exception {
		when(subscriptionDAO.findSubscriptionsToSend(MailCronService.SUBSCRIPTIONS_TO_SEND)).thenReturn(Collections.EMPTY_LIST);

		String result = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();

		assertEquals("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0", result);
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
