package com.picsauditing.service.mail;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.actions.report.ReportApi;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.mail.subscription.ContractorAddedSubscription;
import com.picsauditing.mail.subscription.DynamicReportsSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.VelocityAdaptorTest;
import com.picsauditing.validator.ValidationException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MailCronServiceTest extends PicsTranslationTest {

    private MailCronService mailCronService;
    private int subscriptionId = 123;
    private List<EmailQueue> emails;
    private EmailQueue email;
    @Mock
    private FeatureToggleCheckerGroovy featureToggleChecker;
    @Mock
    private AppPropertyService appPropertyService;
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
        when(emailQueueDAO.getPendingEmails(anyInt())).thenReturn(emails);

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
        when(appPropertyService.isEnabled(MailCronService.SUBSCRIPTION_ENABLE, true)).thenReturn(true);

        mailCronService.processEmailSubscription(subscriptionId);

        verify(builder).sendSubscription(emailSubscription);
    }

    @Test
    public void testProcessEmailSubscription_whenTOGGLE_BPROC_SUBSCRIPTIONEMAILisTrueAndSubscriptionsAreEnabled_doNothing() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(true);
        when(appPropertyService.isEnabled(MailCronService.SUBSCRIPTION_ENABLE, true)).thenReturn(true);

        mailCronService.processEmailSubscription(subscriptionId);

        verify(builder, never()).sendSubscription(emailSubscription);
    }

    @Test
    public void testProcessEmailSubscription_whenTOGGLE_BPROC_SUBSCRIPTIONEMAILisFalseAndSubscriptionsAreNotEnabled_doNothing() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(true);
        when(appPropertyService.isEnabled(MailCronService.SUBSCRIPTION_ENABLE, true)).thenReturn(true);

        mailCronService.processEmailSubscription(subscriptionId);

        verify(builder, never()).sendSubscription(emailSubscription);
    }

    @Test
    public void testProcessEmailSubscription_whenIOException_setSubscriptionToBeReprocessedTomorrow() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)).thenReturn(false);
        when(appPropertyService.isEnabled(MailCronService.SUBSCRIPTION_ENABLE, true)).thenReturn(true);
        doThrow(new IOException()).when(builder).sendSubscription(emailSubscription);

        mailCronService.processEmailSubscription(subscriptionId);

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

        String statusMessage = mailCronService.processPendingEmails();

        verify(emailSender).sendNow(email);
        assertTrue(statusMessage.equals(String.format(MailCronService.SUCCESSFULLY_SENT_EMAILS, 1, 1)));
    }

    @Test
    public void testProcessPendingEmails_whenSendErrorOccurs_processEmailForSendError() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(false);
        doThrow(MessagingException.class).when(emailSender).sendNow(email);

        String statusMessage = mailCronService.processPendingEmails();

        verify(emailSender).sendNow(email);
        assertEquals(EmailStatus.Error, email.getStatus());
        assertEquals(EmailAddressUtils.PICS_ERROR_EMAIL_ADDRESS, email.getToAddresses());
        verify(emailSender).send(email);
        assertTrue(statusMessage.equals(String.format(MailCronService.SUCCESSFULLY_SENT_EMAILS, 0, 1)));
    }

    @Test
    public void testProcessPendingEmails_whenTOGGLE_BPROC_EMAILQUEUEisFalseAndQueueIsEmpty_returnQueueEmptyMessage() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(false);
        when(emailQueueDAO.getPendingEmails(anyInt())).thenReturn(Collections.EMPTY_LIST);

        String statusMessage = mailCronService.processPendingEmails();

        verify(emailSender, never()).sendNow(email);
        assertTrue(statusMessage.equals(MailCronService.THE_EMAIL_QUEUE_IS_EMPTY));
    }

    @Test
    public void testProcessPendingEmails_whenTOGGLE_BPROC_EMAILQUEUEisTrue_doNothing() throws Exception {
        when(featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)).thenReturn(true);

        mailCronService.processPendingEmails();

        verify(emailSender, never()).sendNow(email);
    }

    @Test
    public void testGetSubscriptionIdsToSendAsCommaDelimited() throws Exception {
        Integer[] ids = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        List<Integer> subscriptionIds = Arrays.asList(ids);
        when(subscriptionDAO.findSubscriptionsToSend(anyString(), anyInt())).thenReturn(subscriptionIds);

        String result = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();

        assertEquals("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", result);
    }

    @Test
    public void testGetSubscriptionIdsToSendAsCommaDelimited_WithAppProperty() throws Exception {
        Integer[] ids = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        List<Integer> subscriptionIds = Arrays.asList(ids);
        when(subscriptionDAO.findSubscriptionsToSend(anyString(), anyInt())).thenReturn(subscriptionIds);

        String result = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();

        assertEquals("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15", result);
    }

    @Test
    public void testGetSubscriptionIdsToSendAsCommaDelimited_emptyList() throws Exception {
        when(subscriptionDAO.findSubscriptionsToSend(anyString(), anyInt())).thenReturn(Collections.EMPTY_LIST);

        String result = mailCronService.getSubscriptionIdsToSendAsCommaDelimited();

        assertEquals("0,0,0,0,0,0,0,0,0,0,0,0,0,0,0", result);
    }

    @Ignore("To test the full exception chain, un-ignore this test and add a printStackTrace() in sendEmailSubscription()")
    @Test
    public void testSendEmailSubscription_templateWithABadSubject_shouldLogAHelpfulSubscriptionException_PICS_13365() {
        SpringUtils springUtils = new SpringUtils();
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        springUtils.setApplicationContext(applicationContext);
        TokenDAO tokenDAO = mock(TokenDAO.class);
        when(applicationContext.getBean("TokenDAO")).thenReturn(tokenDAO);

        // Keeping it real as much as possible for exception testing
        SubscriptionBuilder builder = new ContractorAddedSubscription();

        EmailTemplateDAO emailTemplateDAO = mock(EmailTemplateDAO.class);
        Whitebox.setInternalState(builder, "emailTemplateDAO", emailTemplateDAO);
        EmailTemplate emailTemplate = buildEmailTemplateWithABadSubject();
        when(emailTemplateDAO.find(Subscription.AmberFlags.getTemplateID())).thenReturn(emailTemplate);

        OperatorAccount operatorAccount = buildOperatorAccount();
        User user = buildUser(operatorAccount);
        EmailSubscription emailSubscription = buildEmailSubscription(user, Subscription.AmberFlags);

        ContractorAccount contractorAccount = buildContractorAccount();
        ContractorOperator contractorOperator = buildContractorOperator(contractorAccount);

        List<ContractorOperator> contractorOperators = buildContractorOperators(contractorOperator);
        operatorAccount.setContractorOperators(contractorOperators);

        mailCronService.sendEmailSubscription(emailSubscription, builder);
    }

    private List<ContractorOperator> buildContractorOperators(ContractorOperator contractorOperator) {
        List<ContractorOperator> contractorOperators = new ArrayList<>();
        contractorOperators.add(contractorOperator);
        return contractorOperators;
    }

    private User buildUser(OperatorAccount operatorAccount) {
        User user = new User();
        user.setAccount(operatorAccount);
        return user;
    }

    private ContractorOperator buildContractorOperator(ContractorAccount contractorAccount) {
        Date creationDate = mock(Date.class);
        when(creationDate.after(any(Date.class))).thenReturn(true);

        ContractorOperator contractorOperator = new ContractorOperator();
        contractorOperator.setCreationDate(creationDate);
        contractorOperator.setContractorAccount(contractorAccount);
        return contractorOperator;
    }

    private ContractorAccount buildContractorAccount() {
        ContractorAccount contractorAccount = new ContractorAccount();
        contractorAccount.setName("bar");
        contractorAccount.setStatus(AccountStatus.Active);
        return contractorAccount;
    }

    private EmailSubscription buildEmailSubscription(User user, Subscription subscription) {
        EmailSubscription emailSubscription = new EmailSubscription();
        emailSubscription.setUser(user);
        emailSubscription.setTimePeriod(SubscriptionTimePeriod.None);
        emailSubscription.setSubscription(subscription);
        return emailSubscription;
    }

    private OperatorAccount buildOperatorAccount() {
        OperatorAccount operatorAccount = new OperatorAccount();
        operatorAccount.setName("foo");
        return operatorAccount;
    }

    private EmailTemplate buildEmailTemplateWithABadSubject() {
        EmailTemplate template = new EmailTemplate();
        template.setId(10);
        template.setSubject(VelocityAdaptorTest.EmailTemplate_107_translatedBody_sv);
        template.setAllowsVelocity(true);

        return template;
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
