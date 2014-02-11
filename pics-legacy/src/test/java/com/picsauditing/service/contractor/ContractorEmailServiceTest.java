package com.picsauditing.service.contractor;

import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.email.EmailBuilderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContractorEmailServiceTest {

	ContractorEmailService contractorEmailService;
	@Mock
	private User fromContractorUser;
	@Mock
	private ContractorAccount contractorAccount;
	@Mock
	private User currentCsr;
	@Mock
	private EmailSender emailSender;
	@Mock
	private EmailTemplateDAO templateDAO;
	@Mock
	private EmailTemplate messageYourCsrEmailTemplate;
	@Mock
	private EmailBuilderService emailBuilderService;


	@Before
	public void setUp() throws Exception {
		contractorEmailService = new ContractorEmailService();

		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(contractorEmailService, "templateDAO", templateDAO);
		Whitebox.setInternalState(contractorEmailService, "emailBuilderService", emailBuilderService);
		Whitebox.setInternalState(contractorEmailService, "emailSender", emailSender);
		setupMocks();
	}

	@Test
	public void testSendEmailToCsr() throws Exception {
		Map<String, Object> expectedTokenMap = buildExpectedTokenMap();
		EmailQueue emailQueue = buildEmailFromBuilderService();
		when(emailBuilderService.buildEmail(eq(messageYourCsrEmailTemplate), eq(fromContractorUser), eq("joecsr@example.com"), eq(expectedTokenMap))).thenReturn(emailQueue);
		ArgumentCaptor<EmailQueue> actualEmail = ArgumentCaptor.forClass(EmailQueue.class);

		contractorEmailService.sendEmailToCsr("Foo Subject", "This is some message", fromContractorUser);

		verify(emailSender).sendNow(actualEmail.capture());
		assertEquals(messageYourCsrEmailTemplate, actualEmail.getValue().getEmailTemplate());
		assertEquals(fromContractorUser.getEmail(), actualEmail.getValue().getFromAddress());
		assertEquals("joecsr@example.com", actualEmail.getValue().getToAddresses());
		assertEquals("Message Your CSR: Foo Subject", actualEmail.getValue().getSubject());
		assertEquals("*Body text from template*This is some message", actualEmail.getValue().getBody());
	}

	private Map<String, Object> buildExpectedTokenMap() {
		Map<String, Object> expectedTokenMap = new HashMap<>();
		expectedTokenMap.put("contractor", fromContractorUser.getAccount());
		expectedTokenMap.put("user", fromContractorUser);
		return expectedTokenMap;
	}

	private EmailQueue buildEmailFromBuilderService() {
		EmailQueue emailFromBuilderService = new EmailQueue();
		emailFromBuilderService.setEmailTemplate(messageYourCsrEmailTemplate);
		emailFromBuilderService.setFromAddress(fromContractorUser.getEmail());
		emailFromBuilderService.setToAddresses("joecsr@example.com");
		emailFromBuilderService.setSubject("Message Your CSR");
		emailFromBuilderService.setBody("*Body text from template*");
		return emailFromBuilderService;
	}

	private void setupMocks() {
		when(fromContractorUser.getAccount()).thenReturn(contractorAccount);
		when(contractorAccount.isContractor()).thenReturn(true);
		when(contractorAccount.getCurrentCsr()).thenReturn(currentCsr);
		when(currentCsr.getEmail()).thenReturn("joecsr@example.com");

		when(templateDAO.find(EmailTemplate.CONTACT_YOUR_CSR_EMAIL_TEMPLATE)).thenReturn(messageYourCsrEmailTemplate);
		when(messageYourCsrEmailTemplate.getBody()).thenReturn("template_body");
		when(messageYourCsrEmailTemplate.getSubject()).thenReturn("template_subject");
	}
}
