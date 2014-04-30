package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
import com.picsauditing.employeeguard.entities.builders.EmailHashBuilder;
import com.picsauditing.employeeguard.entities.builders.SoftDeletedEmployeeBuilder;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.io.FileUtils;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@UseReporter(DiffReporter.class)
public class EmailServiceTest {

	public static final int APP_USER_ID = 231;

	// Class under test
	private EmailService emailService;

	@Mock
	private ApplicationContext applicationContext;
	@Mock
	private AppPropertyService appPropertyService;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailTemplateDAO emailTemplateDAO;
	@Mock
	private TokenDAO tokenDAO;
	@Mock
	private TranslationService translationService;

	@Before
	public void setUp() throws Exception {
		emailService = new EmailService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(emailService, "emailSender", emailSender);
		Whitebox.setInternalState(emailService, "appPropertyService", appPropertyService);
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);
		Whitebox.setInternalState(TranslationServiceFactory.class, "translationService", translationService);

		when(applicationContext.getBean("EmailTemplateDAO")).thenReturn(emailTemplateDAO);
		when(applicationContext.getBean("TokenDAO")).thenReturn(tokenDAO);
	}

	@Test
	public void testSendEGWelcomeEmail() throws Exception {
		when(emailTemplateDAO.find(anyInt())).thenReturn(buildFakeEGWelcomeEmailTemplate());

		emailService.sendEGWelcomeEmail(new EmailHashBuilder()
				.hash("THIS_IS_THE_HASH_CODE")
				.employee(new SoftDeletedEmployeeBuilder().firstName("Bob The Employee").build())
				.build(),
				"Contractor Account");

		verifyTestSendEGWelcomeEmail();
	}

	private EmailTemplate buildFakeEGWelcomeEmailTemplate() throws IOException {
		EmailTemplate fakeEmailTemplate = new EmailTemplate();

		fakeEmailTemplate.setAllowsVelocity(true);
		fakeEmailTemplate.setHtml(true);
		fakeEmailTemplate.setListType(ListType.Audit);
		fakeEmailTemplate.setSubject("Welcome to PICS EmployeeGUARD");
		fakeEmailTemplate.setBody(FileUtils.readFileToString(new File("src/test/java/com/picsauditing" +
				"/employeeguard/services/EmployeeGUARDWelcomEmailTemplate.html")));

		return fakeEmailTemplate;
	}

	private void verifyTestSendEGWelcomeEmail() throws Exception {
		ArgumentCaptor<EmailQueue> emailQueue = ArgumentCaptor.forClass(EmailQueue.class);
		verify(emailSender).sendNow(emailQueue.capture());

		assertEquals("Welcome to PICS EmployeeGUARD", emailQueue.getValue().getSubject());
		Approvals.verify(emailQueue.getValue().getBody());
	}

	@Test
	public void testSendEGFeedBackEmail() throws Exception {
		when(emailTemplateDAO.find(anyInt())).thenReturn(buildFakeEGFeedBackEmailTemplate());

		boolean result = emailService.sendEGFeedBackEmail("Some feedback", "Contractor Account", APP_USER_ID, "user@email.com");

		verifyTestSendEGFeedBackEmail(result);
	}

	private void verifyTestSendEGFeedBackEmail(boolean result) throws Exception {
		assertTrue(result);

		ArgumentCaptor<EmailQueue> emailQueue = ArgumentCaptor.forClass(EmailQueue.class);
		verify(emailSender).sendNow(emailQueue.capture());

		assertEquals("EmployeeGUARD Feedback", emailQueue.getValue().getSubject());
		Approvals.verify(emailQueue.getValue().getBody());
	}

	@Test
	public void testSendEGFeedBackEmail_Exception() throws Exception {
		when(appPropertyService.getPropertyInt(anyString(), anyInt())).thenThrow(new RuntimeException());

		boolean result = emailService.sendEGFeedBackEmail("Some feedback", "Contractor Account", 231, "user@email.com");

		assertFalse(result);
		verifyZeroInteractions(emailSender);
	}

	private EmailTemplate buildFakeEGFeedBackEmailTemplate() {
		EmailTemplate fakeEmailTemplate = new EmailTemplate();

		fakeEmailTemplate.setAllowsVelocity(true);
		fakeEmailTemplate.setSubject("EmployeeGUARD Feedback");
		fakeEmailTemplate.setBody(" Account Name: ${accountName}\r\n appUserId: ${appUserId}\r\n " +
				"User Email Address: ${userEmailAddress}\r\n Feedback: ${feedback}\r\n\r\n");

		return fakeEmailTemplate;
	}
}
