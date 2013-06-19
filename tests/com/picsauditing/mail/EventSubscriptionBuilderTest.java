package com.picsauditing.mail;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "EventSubscriptionBuilderTest-context.xml" })
public class EventSubscriptionBuilderTest extends PicsTranslationTest {

	@Mock
	private EmailBuilder emailBuilder;
	@Mock
	private EmailQueue email;
	@Mock
	private EmailTemplate emailTemplate;

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
