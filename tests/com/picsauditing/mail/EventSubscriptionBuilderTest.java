package com.picsauditing.mail;

import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Currency;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"EventSubscriptionBuilderTest-context.xml"})
public class EventSubscriptionBuilderTest {
	private EventSubscriptionBuilder eventSubscriptionBuilder;

	@Mock private EmailBuilder emailBuilder;
	@Mock private EmailQueue email;
	@Mock private EmailTemplate emailTemplate;

	@Autowired private EmailSenderSpring emailSender;
	@Autowired private NoteDAO noteDAO;
	@Autowired private EmailSubscriptionDAO emailSubscriptionDAO;
	@Autowired private EmailTemplateDAO emailTemplateDAO;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		emailTemplateDAO = (EmailTemplateDAO) SpringUtils.getBean("EmailTemplateDAO");
		noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		emailSender = (EmailSenderSpring) SpringUtils.getBean("EmailSenderSpring");
		emailSubscriptionDAO = (EmailSubscriptionDAO) SpringUtils.getBean("EmailSubscriptionDAO");
	}
	@Test
	public void testGetBillingEmail_UDS() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EventSubscriptionBuilder.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.USD);
		assertEquals("\"PICS Billing\"<billing@picsauditing.com>", email);
	}
	@Test
	public void testGetBillingEmail_CAD() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EventSubscriptionBuilder.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.CAD);
		assertEquals("\"PICS Billing\"<billing@picsauditing.com>", email);
	}
	@Test
	public void testGetBillingEmail_GBP() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EventSubscriptionBuilder.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.GBP);
		assertEquals("\"PICS Billing\"<eubilling@picsauditing.com>", email);
	}
	@Test
	public void testGetBillingEmail_EUR() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Method method = EventSubscriptionBuilder.class.getDeclaredMethod("getBillingEmail", Currency.class);
		method.setAccessible(true);
		String email = (String) method.invoke(EventSubscriptionBuilder.class, Currency.EUR);
		assertEquals("\"PICS Billing\"<eubilling@picsauditing.com>", email);
	}
	@Test
	public void testNotifyUpcomingImplementationAudit() throws Exception {
		ContractorAudit audit = getAudit();

		when(emailTemplateDAO.find(Mockito.anyInt())).thenReturn(emailTemplate);
		Mockito.doNothing().when(emailBuilder).clear();
		Mockito.doNothing().when(emailBuilder).setTemplate(emailTemplate);
		Mockito.doNothing().when(emailBuilder).setConID(audit.getContractorAccount().getId());
		Mockito.doNothing().when(emailBuilder).addToken("contractor", audit.getContractorAccount());
		Mockito.doNothing().when(emailBuilder).setToAddresses(audit.getContractorAccount().getActiveUser().getEmail());
		Mockito.doNothing().when(emailBuilder).setUser(audit.getContractorAccount().getActiveUser());
		Mockito.doNothing().when(emailBuilder).setFromAddress("audits@picsauditing.com");
		when(emailBuilder.build()).thenReturn(email);

		EventSubscriptionBuilder.notifyUpcomingImplementationAudit(audit);

//		verify(emailSender).send(email);
//		verify(noteDAO).save(any(Note.class));
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
