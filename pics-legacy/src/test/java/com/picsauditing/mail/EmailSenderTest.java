package com.picsauditing.mail;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.EmailAddressUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.EmailQueueDAO;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.MessagingException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "EmailSenderTest-context.xml" })
public class EmailSenderTest {
	private EmailSender emailSenderSpring;
	
	@Mock private EmailQueue email;
	@Mock private EmailQueueDAO emailQueueDAO;
	@Mock private EmailTemplate emailTemplate;
	@Mock private ContractorAccount contractorAccount;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		emailSenderSpring = new EmailSender();
		
		PicsTestUtil.autowireDAOsFromDeclaredMocks(emailSenderSpring, this);
	}

    @Test
    public void testIsEmailAddressValid_BillingAddress() throws Exception {
        Boolean result = Whitebox.invokeMethod(emailSenderSpring, "isEmailAddressValid", EmailAddressUtils.getBillingEmail(Currency.USD));

        assertTrue(result);
    }

	@Test
	public void testCheckDeactivated_NullContractorAccountReturnsFalse() throws Exception {
		when(email.getContractorAccount()).thenReturn((ContractorAccount)null);
		
		Boolean result = Whitebox.invokeMethod(emailSenderSpring, "checkDeactivated", email);
		
		assertFalse(result);
	}

	@Test
	public void testCheckDeactivated_AccountIsActiveReturnsFalse() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Active);

		Boolean result = Whitebox.invokeMethod(emailSenderSpring, "checkDeactivated", email);
		
		assertFalse(result);
	}

	@Test
	public void testCheckDeactivated_NullEmailTemplateReturnsFalse() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(email.getEmailTemplate()).thenReturn((EmailTemplate)null);

		Boolean result = Whitebox.invokeMethod(emailSenderSpring, "checkDeactivated", email);
		
		assertFalse(result);
	}
	
	@Test
	public void testCheckDeactivated_ContractorIsDeactivatedTemplateValidToSend() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(email.getEmailTemplate()).thenReturn(emailTemplate);
		when(emailTemplate.getId()).thenReturn(EmailTemplate.VALID_DEACTIVATED_EMAILS().iterator().next());

		Boolean result = Whitebox.invokeMethod(emailSenderSpring, "checkDeactivated", email);
		
		assertFalse(result);
	}

	@Test
	public void testCheckDeactivated_ContractorIsDeactivatedTemplateIsNotValidToSend() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(email.getEmailTemplate()).thenReturn(emailTemplate);
		when(emailTemplate.getId()).thenReturn(notValidDeactivatedEmailId());

		Boolean result = Whitebox.invokeMethod(emailSenderSpring, "checkDeactivated", email);
		
		assertTrue(result);
		verify(email).setStatus(EmailStatus.Error);
		verify(email).setSentDate((Date)any());
		verify(emailQueueDAO).save(email);
	}

    @Test
    public void testSendNow_ValidToEmailAddress() throws MessagingException {
        email = EmailQueue.builder()
                .toAddress("contractor@example.com")
                .fromAddress("pics@example.com")
                .emailTemplate(emailTemplate)
                .build();

        emailSenderSpring.sendNow(email);

        assertEquals(EmailStatus.Sent, email.getStatus());
		assertNotNull(email.getSentDate());
		verify(emailQueueDAO).save(email);

    }

    @Test
    public void testSendNow_ValidToEmailAddress_MultipleRecipient() throws MessagingException {
        email = EmailQueue.builder()
                .toAddress("contractor1@example.com, contractor2@example.com,contractor3@example.com")
                .fromAddress("pics@example.com")
                .emailTemplate(emailTemplate)
                .build();

        emailSenderSpring.sendNow(email);

        assertEquals(EmailStatus.Sent, email.getStatus());
        assertNotNull(email.getSentDate());
        verify(emailQueueDAO).save(email);
    }


    @Test
    public void testSendNow_InvalidToEmailAddress() throws MessagingException {
        email = EmailQueue.builder()
                .toAddress("contractor$example.com")
                .fromAddress("pics@example.com")
                .emailTemplate(emailTemplate)
                .build();

        emailSenderSpring.sendNow(email);

        assertEquals(EmailStatus.Error, email.getStatus());
        assertNotNull(email.getSentDate());
        verify(emailQueueDAO).save(email);
    }

    @Test
    public void testSendNow_InvalidToEmailAddress_MultipleRecipient() throws MessagingException {
        email = EmailQueue.builder()
                .toAddress("contractor1@example.com, contractor2#example.com,contractor3@example.com")
                .fromAddress("pics@example.com")
                .emailTemplate(emailTemplate)
                .build();

        emailSenderSpring.sendNow(email);

        assertEquals(EmailStatus.Error, email.getStatus());
        assertNotNull(email.getSentDate());
        verify(emailQueueDAO).save(email);
    }

    @Test
    public void testSendNow_InvalidToEmailAddress_nullEmail() throws MessagingException {
        email = EmailQueue.builder()
                .toAddress(null)
                .fromAddress("pics@example.com")
                .emailTemplate(emailTemplate)
                .build();

        emailSenderSpring.sendNow(email);

        assertEquals(EmailStatus.Error, email.getStatus());
        assertNotNull(email.getSentDate());
        verify(emailQueueDAO).save(email);
    }

    private int notValidDeactivatedEmailId() {
		int i = 1;
		while(EmailTemplate.VALID_DEACTIVATED_EMAILS().contains(i)) {
			i++;
		}
		return i;
	}
	
	@Test
	public void testCheckDeactivated_X() throws Exception {
	}

}
