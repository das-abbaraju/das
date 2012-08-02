package com.picsauditing.mail;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.Matchers.*;

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

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.search.Database;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"EventSubscriptionBuilderTest-context.xml"})
public class EventSubscriptionBuilderTest {

	@Mock private EmailBuilder emailBuilder;
	@Mock private EmailQueue email;
	@Mock private EmailTemplate emailTemplate;
	@Mock private Database databaseForTesting;

	@Autowired private EmailSender emailSender;
	@Autowired private NoteDAO noteDAO;
	@Autowired private EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired private EmailTemplateDAO emailTemplateDAO;
	
	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database)null);
		Whitebox.setInternalState(EventSubscriptionBuilder.class, "emailBuilder", (EmailBuilder)null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
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

	private ContractorAudit getAudit(){
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
