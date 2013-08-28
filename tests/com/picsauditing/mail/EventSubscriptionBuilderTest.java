package com.picsauditing.mail;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.EntityFactory;
import com.picsauditing.actions.contractors.ContractorCronStatistics;
import com.picsauditing.jpa.entities.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.NoteDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "EventSubscriptionBuilderTest-context.xml" })
public class EventSubscriptionBuilderTest extends PicsTranslationTest {

	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private EmailQueue email;
	@Mock
	private EmailTemplate emailTemplate;
    @Mock
    private ContractorCronStatistics cronStats;

	@Autowired
	private EmailSender emailSender;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired
	private EmailTemplateDAO emailTemplateDAO;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(EventSubscriptionBuilder.class, "emailBuilder", (EmailBuilder) null);
		PicsTranslationTest.tearDownTranslationService();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		super.resetTranslationService();
		Whitebox.setInternalState(EventSubscriptionBuilder.class, "emailBuilder", emailBuilder);

		when(emailBuilder.build()).thenReturn(email);
	}

	@Test
	public void testNotifyUpcomingImplementationAudit() throws Exception {
		ContractorAudit audit = getAudit();
		when(emailTemplateDAO.find(Mockito.anyInt())).thenReturn(emailTemplate);

		EventSubscriptionBuilder.notifyUpcomingImplementationAudit(audit);

		verify(emailSender).send(email);
	}

    @Test
    public void testEmailCronFailure() throws Exception {
        when(cronStats.isEmailCronError()).thenReturn(true);
        List<EmailSubscription> subscriptions = new ArrayList<EmailSubscription>();
        subscriptions.add(createEmailSubscription(Subscription.EmailCronFailure, SubscriptionTimePeriod.None, null));
        when(emailSubscriptionDAO.find(Subscription.EmailCronFailure, Account.PicsID)).thenReturn(subscriptions);
        EventSubscriptionBuilder.theSystemIsDown(cronStats);
        verify(emailBuilder, times(0)).build();
    }

    private EmailSubscription createEmailSubscription(Subscription subscription, SubscriptionTimePeriod period, Date lastSent) {
        EmailSubscription es = new EmailSubscription();
        es.setSubscription(subscription);
        es.setTimePeriod(period);
        es.setLastSent(lastSent);

        User user = EntityFactory.makeUser();
        user.setEmail("tester@picsauditing.com");
        es.setUser(user);
        return es;
    }

	private ContractorAudit getAudit() {
		ContractorAudit audit = new ContractorAudit();
		User user = new User();
		ContractorAccount contractorAccount = new ContractorAccount();

		user.setEmail("test@picsauditing.com");
		List<User> users = new ArrayList<User>();
		users.add(user);
		contractorAccount.setUsers(users);
		contractorAccount.setId(99);
		contractorAccount.setName("junit test");
		audit.setContractorAccount(contractorAccount);
		return audit;
	}
}
