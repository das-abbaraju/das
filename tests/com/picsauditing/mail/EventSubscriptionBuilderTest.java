package com.picsauditing.mail;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.util.SpringUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"EventSubscriptionBuilderTest-context.xml"})
public class EventSubscriptionBuilderTest {
	private EventSubscriptionBuilder eventSubscriptionBuilder;
	
	@Mock private ContractorAudit audit;
	@Mock private EmailBuilder emailBuilder;
	@Mock private EmailQueue emailQueue;
	@Mock private TranslationActionSupport tas;
	@Mock private EmailTemplate et;		

	@Autowired private EmailSenderSpring emailSender;
	@Autowired private NoteDAO noteDAO;
	@Autowired private EmailSubscriptionDAO emailSubscriptionDAO;
	
	@Before
	public void setUp() throws Exception {
	
		MockitoAnnotations.initMocks(this);

//		when(tas.getText(any(String.class))).thenReturn(any(String.class));
//		verify(et).setSubject(any(String.class));
//		verify(et).setBody(any(String.class));		
//		when(emailBuilder.build()).thenReturn(emailQueue);
//		
//		private static EmailSubscriptionDAO subscriptionDAO = (EmailSubscriptionDAO) SpringUtils
//				.getBean("EmailSubscriptionDAO");
//		private static NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
//		private static EmailSenderSpring emailSender = (EmailSenderSpring) SpringUtils.getBean("EmailSenderSpring");
//		
//		when(springUtils.getBean("EmailSubscriptionDAO")).thenReturn(EmailSubscriptionDAO.class);
//		
//		Whitebox.setInternalState(eventSubscriptionBuilder, "audit", audit);		
//		Whitebox.setInternalState(eventSubscriptionBuilder, "tas", tas);
//		Whitebox.setInternalState(eventSubscriptionBuilder, "emailBuilder", emailBuilder);
//		Whitebox.setInternalState(eventSubscriptionBuilder, "emailSender", emailSender);		
//		Whitebox.setInternalState(eventSubscriptionBuilder, "emailQueue", emailQueue);
//		Whitebox.setInternalState(eventSubscriptionBuilder, "et", et);
//		Whitebox.setInternalState(eventSubscriptionBuilder, "noteDAO", noteDAO);
	}

	@Test
	public void testNotifyUpcomingImplementationAudit() throws Exception {
		EventSubscriptionBuilder.notifyUpcomingImplementationAudit(audit);
//		verify(emailSender).send(emailQueue);
//		verify(noteDAO).save(any(Note.class));
	}
	
	@Test
	public void testSendInvalidEmailsToAudits() throws Exception{
//		Whitebox.invokeMethod(eventSubscriptionBuilder, "sendInvalidEmailsToAudits", audit);
//		verify(emailSender).send(emailQueue);
//		verify(noteDAO).save(any(Note.class));
	}
}
