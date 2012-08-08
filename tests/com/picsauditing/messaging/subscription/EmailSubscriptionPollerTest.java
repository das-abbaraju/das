package com.picsauditing.messaging.subscription;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.picsauditing.messaging.MessageHandler;
import com.picsauditing.toggle.FeatureToggleChecker;


public class EmailSubscriptionPollerTest {
	private EmailSubscriptionPoller emailSubscriptionPoller;
	private static final String featureToggleName = "Toggle.Test";
	
	@Mock private RabbitTemplate amqpTemplate;
	@Mock private MessageHandler emailSubscriptionHandler;
	@Mock private FeatureToggleChecker featureToggleChecker;
	@Mock private Message message;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		emailSubscriptionPoller = new EmailSubscriptionPoller();
		
		emailSubscriptionPoller.setEmailSubscriptionHandler(emailSubscriptionHandler);
		emailSubscriptionPoller.setAmqpTemplate(amqpTemplate);
		emailSubscriptionPoller.setFeatureToggleChecker(featureToggleChecker);
		emailSubscriptionPoller.setFeatureToggleName(featureToggleName);
		
		System.clearProperty("pics.activate_subscription_cron");
	}
	
	@Test
	public void testPollForMessage_HappyPathToHandle() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(featureToggleName)).thenReturn(true);
		System.setProperty("pics.activate_subscription_cron", "1");
		when(amqpTemplate.receive()).thenReturn(message);
		
		emailSubscriptionPoller.pollForMessage();
		
		verify(amqpTemplate).receive();
		verify(emailSubscriptionHandler).handle(message);
	}
	
	@Test
	public void testPollForMessage_SystemPropertySetToActivateOnThisAppServerNullMessage_DoesNotHandle() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(featureToggleName)).thenReturn(true);
		System.setProperty("pics.activate_subscription_cron", "1");
		
		emailSubscriptionPoller.pollForMessage();
		
		verify(amqpTemplate).receive();
		verifyZeroInteractions(emailSubscriptionHandler);
	}
	
	@Test
	public void testPollForMessage_SystemPropertyNotSetToActivateOnThisAppServer_DoesNothing() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(featureToggleName)).thenReturn(true);
		
		emailSubscriptionPoller.pollForMessage();
		
		verifyZeroInteractions(amqpTemplate, emailSubscriptionHandler);
	}
	
	@Test
	public void testPollForMessage_FeatureNotEnabledDoesNothing() throws Exception {
		when(featureToggleChecker.isFeatureEnabled(featureToggleName)).thenReturn(false);
		
		emailSubscriptionPoller.pollForMessage();
		
		verifyZeroInteractions(amqpTemplate, emailSubscriptionHandler);
	}
}
