package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.AppPropertyService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailServiceTest {

	// Class under test
	private EmailService emailService;

	@Mock
	private EmailSender emailSender;
	@Mock
	private AccountDAO accountDAO;
	@Mock
	private AppPropertyService appPropertyService;

	@Before
	public void setUp() throws Exception {
		emailService = new EmailService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(emailService, "emailSender", emailSender);
		Whitebox.setInternalState(emailService, "accountDAO", accountDAO);
		Whitebox.setInternalState(emailService, "appPropertyService", appPropertyService);
	}

	@Test
	public void testSendEGWelcomeEmail() throws Exception {
		fail("not implemented yet");
	}

	@Test
	public void testSendEGFeedBackEmail() throws Exception {
		

		verify(emailSender).send(any(EmailQueue.class));
	}

	@Test
	public void testSendEGFeedBackEmail_Exception() throws Exception {
		when(appPropertyService.getPropertyString(anyString())).thenThrow(new RuntimeException());

		boolean result = emailService.sendEGFeedBackEmail("Some feedback", "Contractor Account", 231, "user@email.com");

		assertFalse(result);
	}
}
