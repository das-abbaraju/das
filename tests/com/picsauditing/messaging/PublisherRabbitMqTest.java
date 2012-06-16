package com.picsauditing.messaging;

import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;


public class PublisherRabbitMqTest {
	private PublisherRabbitMq publisherRabbitMq;
	@Mock private RabbitTemplate amqpTemplate;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		publisherRabbitMq = new PublisherRabbitMq();
		publisherRabbitMq.setAmqpTemplate(amqpTemplate);
	}

	@Test
	public void testSendCriticalAlertMessage() throws Exception {
		FlagChange fc = getFlagChange();
		publisherRabbitMq.publish(fc);
		
		verify(amqpTemplate, times(0)).convertAndSend(fc);
	}

	public FlagChange getFlagChange() {
		FlagChange fc = new FlagChange();
		fc.setContractor(new ContractorAccount(3));
		fc.setOperator(new OperatorAccount());
		fc.setFromColor(FlagColor.Red);
		fc.setToColor(FlagColor.Green);
		fc.setTimestamp(new Date());
		
		return fc;
	}
}
