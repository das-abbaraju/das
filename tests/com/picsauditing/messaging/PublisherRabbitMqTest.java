package com.picsauditing.messaging;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


public class PublisherRabbitMqTest {
	private PublisherRabbitMq publisherRabbitMq;
	@Mock private EnterpriseMessage message;
	@Mock private RabbitTemplate amqpTemplate;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		publisherRabbitMq = new PublisherRabbitMq();
		publisherRabbitMq.setAmqpTemplate(amqpTemplate);
		
		when(message.getMessage()).thenReturn("message");
	}

	@Test
	public void testSendCriticalAlertMessage() throws Exception {
		publisherRabbitMq.publish(message);
		
		verify(amqpTemplate, times(0)).convertAndSend(message.getMessage());
	}

}
