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
import com.picsauditing.mail.subscription.*;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.VelocityAdaptorTest;
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
	private User user;

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

	    user = new User();
	    user.setEmail("foo@example.com");

        when(subscriptionFactory.getBuilder((Subscription) any())).thenReturn(builder);
        when(subscriptionDAO.find(subscriptionId)).thenReturn(emailSubscription);
        when(emailSubscription.getReport()).thenReturn(report);
        when(emailSubscription.getSubscription()).thenReturn(Subscription.AmberFlags);
        when(emailSubscription.getUser()).thenReturn(user);

        when(emailQueueDAO.getPendingEmails(anyInt())).thenReturn(emails);

        Whitebox.setInternalState(mailCronService, "featureToggleChecker", featureToggleChecker);
        Whitebox.setInternalState(mailCronService, "appPropertyService", appPropertyService);
        Whitebox.setInternalState(mailCronService, "subscriptionFactory", subscriptionFactory);
        Whitebox.setInternalState(mailCronService, "emailSender", emailSender);
        Whitebox.setInternalState(mailCronService, "subscriptionDAO", subscriptionDAO);
        Whitebox.setInternalState(mailCronService, "emailQueueDAO", emailQueueDAO);
    }

    @Test
    public void testProcessEmailSubscription_whenIOException_setSubscriptionToBeReprocessedTomorrow() throws Exception {
        when(appPropertyService.isEnabled(MailCronService.SUBSCRIPTION_ENABLE, true)).thenReturn(true);
        doThrow(new IOException()).when(builder).sendSubscription(emailSubscription);

        mailCronService.processEmailSubscription(subscriptionId);

        verify(emailSubscription).setLastSent(any(Date.class));
        verify(subscriptionDAO).save(emailSubscription);
    }

    @Test(expected = SubscriptionValidationException.class)
    public void testValidateEmailSubscription_nullEmailSubscriptionShouldThrowValidationError() throws Exception {
        EmailSubscription invalidEmailSubscription = null;

        mailCronService.validateEmailSubscription(invalidEmailSubscription, subscriptionId);
    }

    @Test(expected = SubscriptionValidationException.class)
    public void testValidateEmailSubscription_emailSubscriptionWithNullSubscriptionShouldThrowValidationError() throws Exception {
        EmailSubscription invalidEmailSubscription = createEmailSubscription(null, new Report());

        mailCronService.validateEmailSubscription(invalidEmailSubscription, subscriptionId);
    }

	@Test(expected = SubscriptionValidationException.class)
	public void testValidateEmailSubscription_emailSubscriptionWithNullUserShouldThrowValidationError() throws Exception {
		EmailSubscription invalidEmailSubscription = createEmailSubscription(Subscription.AmberFlags, null);
		invalidEmailSubscription.setUser(null);

		mailCronService.validateEmailSubscription(invalidEmailSubscription, subscriptionId);
	}

	@Test(expected = SubscriptionValidationException.class)
	public void testValidateEmailSubscription_emailSubscriptionWithNullUserEmailShouldThrowValidationError() throws Exception {
		EmailSubscription invalidEmailSubscription = createEmailSubscription(Subscription.AmberFlags, null);
		invalidEmailSubscription.getUser().setEmail(null);

		mailCronService.validateEmailSubscription(invalidEmailSubscription, subscriptionId);
	}

	@Test(expected = SubscriptionValidationException.class)
	public void testValidateEmailSubscription_emailSubscriptionWithBlankUserEmailShouldThrowValidationError() throws Exception {
		EmailSubscription invalidEmailSubscription = createEmailSubscription(Subscription.AmberFlags, null);
		invalidEmailSubscription.getUser().setEmail("");

		mailCronService.validateEmailSubscription(invalidEmailSubscription, subscriptionId);
	}

    @Test
    public void testFindEmailSubscription() throws Exception {

        mailCronService.findEmailSubscription(subscriptionId);

        verify(subscriptionDAO).find(subscriptionId);
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
	    User user = new User();
	    user.setEmail("foo@example.com");
	    EmailSubscription emailSubscription = new EmailSubscription();
	    emailSubscription.setUser(user);
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
