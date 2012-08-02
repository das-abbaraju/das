package com.picsauditing.mail;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.messaging.Publisher;
import com.picsauditing.toggle.FeatureToggleChecker;


public class EmailSenderTest {
	private EmailSender emailSenderSpring;
	
	@Mock private EmailQueue email;
	@Mock private EmailQueueDAO emailQueueDAO;
	@Mock private EmailTemplate emailTemplate;
	@Mock private ContractorAccount contractorAccount;
	@Mock private Publisher emailQueuePublisher;
	@Mock private FeatureToggleChecker featureToggleChecker;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		emailSenderSpring = new EmailSender();
		
		emailSenderSpring.setFlagChangePublisher(emailQueuePublisher);
		PicsTestUtil.autowireDAOsFromDeclaredMocks(emailSenderSpring, this);
		Whitebox.setInternalState(emailSenderSpring, "featureToggleChecker", featureToggleChecker);
	}
	@Test
	public void testPublishEnterpriseMessageIfEmailShouldBeSent_NormalPriorityEmail() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Active);
		when(email.getPriority()).thenReturn((EmailQueue.HIGH_PRIORITY-10));

		Whitebox.invokeMethod(emailSenderSpring, "publishEnterpriseMessageIfEmailShouldBeSent", email);

		verify(emailQueuePublisher).publish(email, "email-queue-normal");
	}
	
	@Test
	public void testPublishEnterpriseMessageIfEmailShouldBeSent_HighPriorityEmail() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Active);
		when(email.getPriority()).thenReturn((EmailQueue.HIGH_PRIORITY+10));

		Whitebox.invokeMethod(emailSenderSpring, "publishEnterpriseMessageIfEmailShouldBeSent", email);

		verify(emailQueuePublisher).publish(email, "email-queue-priority");
	}

	@Test
	public void testPublishEnterpriseMessageIfEmailShouldBeSent_ContractorActiveShouldPublishMessage() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Active);

		Whitebox.invokeMethod(emailSenderSpring, "publishEnterpriseMessageIfEmailShouldBeSent", email);

		verify(emailQueuePublisher).publish(eq(email), anyString());
	}

	@Test
	public void testPublishEnterpriseMessageIfEmailShouldBeSent_ContractorNotActiveButValidDeactivatedTemplateShouldPublishMessage() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(email.getEmailTemplate()).thenReturn(emailTemplate);
		when(emailTemplate.getId()).thenReturn(EmailTemplate.VALID_DEACTIVATED_EMAILS().iterator().next());

		Whitebox.invokeMethod(emailSenderSpring, "publishEnterpriseMessageIfEmailShouldBeSent", email);

		verify(emailQueuePublisher).publish(eq(email), anyString());
	}

	@Test
	public void testPublishEnterpriseMessageIfEmailShouldBeSent_ContractorNotActiveNotValidDeactivatedFeatureEnabledTemplateShouldNotPublishMessage() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(email.getEmailTemplate()).thenReturn(emailTemplate);
		when(emailTemplate.getId()).thenReturn(notValidDeactivatedEmailId());
		when(featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.EmailQueue")).thenReturn(true);

		Whitebox.invokeMethod(emailSenderSpring, "publishEnterpriseMessageIfEmailShouldBeSent", email);

		verify(emailQueuePublisher, never()).publish(eq(email), anyString());
	}

	@Test
	public void testPublishEnterpriseMessageIfEmailShouldBeSent_ContractorNotActiveNotValidDeactivatedFeatureDisabledTemplateShouldNotPublishMessage() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(email.getEmailTemplate()).thenReturn(emailTemplate);
		when(emailTemplate.getId()).thenReturn(notValidDeactivatedEmailId());
		when(featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.EmailQueue")).thenReturn(false);

		Whitebox.invokeMethod(emailSenderSpring, "publishEnterpriseMessageIfEmailShouldBeSent", email);

		verify(emailQueuePublisher, never()).publish(eq(email), anyString());
	}
	
	@Test
	public void testPublishEnterpriseMessageIfEmailShouldBeSent_ContractorNotActiveNotValidEnabledFeatureDisabledTemplateShouldLogError() throws Exception {
		when(email.getContractorAccount()).thenReturn(contractorAccount);
		when(contractorAccount.getStatus()).thenReturn(AccountStatus.Deactivated);
		when(email.getEmailTemplate()).thenReturn(emailTemplate);
		when(emailTemplate.getId()).thenReturn(notValidDeactivatedEmailId());
		when(featureToggleChecker.isFeatureEnabled("Toggle.BackgroundProcesses.EmailQueue")).thenReturn(true);

		Whitebox.invokeMethod(emailSenderSpring, "publishEnterpriseMessageIfEmailShouldBeSent", email);

		verify(email).setStatus(EmailStatus.Error);
		verify(email).setSentDate((Date)any());
		verify(emailQueueDAO).save(email);
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
