package com.picsauditing.messaging.subscription;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.amqp.core.Message;

import com.ibm.icu.text.SimpleDateFormat;
import com.picsauditing.PicsTestUtil;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.subscription.MissingSubscriptionException;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;


public class EmailSubscriptionHandlerTest {
	private EmailSubscriptionHandler emailSubscriptionHandler;
	
	@Mock private AppPropertyDAO appPropertyProvider;
	@Mock private EmailSubscriptionDAO emailSubscriptionProvider;
	@Mock private SubscriptionBuilderFactory subscriptionFactory;
	@Mock private SubscriptionBuilder builder;
	@Mock private Message message;
	@Mock private EmailSubscription emailSubscription;
	@Mock private Logger logger;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		emailSubscriptionHandler = new EmailSubscriptionHandler(); 
		
		PicsTestUtil.autowireDAOsFromDeclaredMocks(emailSubscriptionHandler, this);
		when(appPropertyProvider.getProperty("subscription.enable")).thenReturn("true");
		when(emailSubscriptionProvider.find(0)).thenReturn((EmailSubscription)null);
		when(emailSubscriptionProvider.find(1)).thenReturn(emailSubscription);
		Whitebox.setInternalState(emailSubscriptionHandler, "subscriptionFactory", subscriptionFactory);
		Whitebox.setInternalState(emailSubscriptionHandler, "emailSubscriptionProvider", emailSubscriptionProvider);
		Whitebox.setInternalState(emailSubscriptionHandler, "logger", logger);
	}
	
	@Test
	public void testHandle_HappyPath() throws Exception {
		when(appPropertyProvider.getProperty("subscription.enable")).thenReturn("true");
		when(message.getBody()).thenReturn("1".getBytes());
		when(subscriptionFactory.getBuilder((Subscription)any())).thenReturn(builder);
		
		emailSubscriptionHandler.handle(message);

		verify(builder).sendSubscription(emailSubscription);
	}
	
	@Test
	public void testHandle_SubscriptionFactoryExceptionResetsSubscriptionForReprocessingTomorrow() throws Exception {
		when(appPropertyProvider.getProperty("subscription.enable")).thenReturn("true");
		when(message.getBody()).thenReturn("1".getBytes());
		doThrow(new MissingSubscriptionException()).when(subscriptionFactory).getBuilder((Subscription)any());
		
		emailSubscriptionHandler.handle(message);

		verify(emailSubscriptionProvider).save(emailSubscription);
		
		verify(emailSubscription).setLastSent((Date)any());
	}
	
	@Ignore
	public void testHandle_BuilderExceptionResetsSubscriptionForReprocessingTomorrow() throws Exception {
		when(appPropertyProvider.getProperty("subscription.enable")).thenReturn("true");
		when(message.getBody()).thenReturn("1".getBytes());
		when(subscriptionFactory.getBuilder((Subscription)any())).thenReturn(builder);
		doThrow(new IOException()).when(builder).sendSubscription(emailSubscription);
		
		emailSubscriptionHandler.handle(message);

		ArgumentCaptor<Date> argument = ArgumentCaptor.forClass(Date.class);
		verify(emailSubscription).setLastSent(argument.capture());
		SimpleDateFormat format = new SimpleDateFormat("d");
		Integer dayToday = new Integer(format.format(new Date()));
		Integer dayTomorrow = new Integer(format.format(argument.getValue()));
		assertTrue(((Integer)dayTomorrow).equals((dayToday+1)));
		verify(emailSubscriptionProvider).save(emailSubscription);
	}
	
	@Test
	public void testHandle_NoFoundSubscriptionDoesNothing() throws Exception {
		when(appPropertyProvider.getProperty("subscription.enable")).thenReturn("true");
		when(message.getBody()).thenReturn("0".getBytes());
		
		emailSubscriptionHandler.handle(message);
		
		verifyZeroInteractions(subscriptionFactory, builder);
		verify(logger).error(anyString(), eq(0));
	}
	
	@Test
	public void testHandle_SubscriptionNotEnabledDoesNothing() throws Exception {
		when(appPropertyProvider.getProperty("subscription.enable")).thenReturn("false");
		
		emailSubscriptionHandler.handle(message);
		
		verifyZeroInteractions(emailSubscriptionProvider, subscriptionFactory, builder);
	}
	
	@Test
	public void testHandle_NullMessageDoesNothing() throws Exception {
		emailSubscriptionHandler.handle((Message)null);
		
		verifyZeroInteractions(appPropertyProvider, emailSubscriptionProvider, subscriptionFactory, builder);
	}
}
