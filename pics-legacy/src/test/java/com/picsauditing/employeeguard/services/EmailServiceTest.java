package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.TokenDAO;
import com.picsauditing.i18n.service.TranslationService;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.Token;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.SpringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailServiceTest {

	// Class under test
	private EmailService emailService;


	@Mock
	private AccountDAO accountDAO;
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
		Whitebox.setInternalState(emailService, "accountDAO", accountDAO);
		Whitebox.setInternalState(emailService, "appPropertyService", appPropertyService);
		Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);
		Whitebox.setInternalState(TranslationServiceFactory.class, "translationService", translationService);

		when(applicationContext.getBean("EmailTemplateDAO")).thenReturn(emailTemplateDAO);
		when(applicationContext.getBean("TokenDAO")).thenReturn(tokenDAO);
		when(emailTemplateDAO.find(anyInt())).thenReturn(new EmailTemplate());
	}

//	public void setTemplate(int id) {
//		EmailTemplateDAO dao = SpringUtils.getBean("EmailTemplateDAO");
//		setTemplate(dao.find(id));
//	}
//
//	private List<Token> getPicsTags() {
//		if (picsTags == null) {
//			TokenDAO dao = SpringUtils.getBean("TokenDAO");
//			picsTags = dao.findByType(template.getListType());
//		}
//		return picsTags;
//	}


	@Test
	public void testSendEGWelcomeEmail() throws Exception {
		fail("not implemented yet");
	}

	@Test
	public void testSendEGFeedBackEmail() throws Exception {

		emailService.sendEGFeedBackEmail("Some feedback", "Contractor Account", 231, "user@email.com");

		verify(emailSender).send(any(EmailQueue.class));
	}

	@Test
	public void testSendEGFeedBackEmail_Exception() throws Exception {
		when(appPropertyService.getPropertyString(anyString())).thenThrow(new RuntimeException());

		boolean result = emailService.sendEGFeedBackEmail("Some feedback", "Contractor Account", 231, "user@email.com");

		assertFalse(result);
	}
}
