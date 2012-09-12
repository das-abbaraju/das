package com.picsauditing.messaging;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.toggle.FeatureToggle;

public class PublisherRabbitMqTest {
	private PublisherRabbitMq publisherRabbitMq;
	@Mock
	private RabbitTemplate amqpTemplate;
	@Mock
	private FeatureToggle featureToggleChecker;
	@Mock
	private Logger logger;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		publisherRabbitMq = new PublisherRabbitMq();

		Whitebox.setInternalState(publisherRabbitMq, "amqpTemplate", amqpTemplate);
		Whitebox.setInternalState(publisherRabbitMq, "featureToggleChecker", featureToggleChecker);
		Whitebox.setInternalState(publisherRabbitMq, "logger", logger);

		when(featureToggleChecker.isFeatureEnabled(anyString())).thenReturn(true);
	}

	@Test
	public void testSendCriticalAlertMessage_ValidFlagChangeGetsSent() throws Exception {
		FlagChange fc = getFlagChange();
		publisherRabbitMq.publish(fc);

		verify(amqpTemplate).convertAndSend(fc);
		verify(logger, never()).error(anyString(), any(Exception.class));
	}

	@Test
	public void testSendCriticalAlertMessage_InvalidFlagChangeIsLogged() throws Exception {
		doThrow(new AmqpException("test amqp exception")).when(amqpTemplate).convertAndSend(any(FlagChange.class));

		publisherRabbitMq.publish(null);

		verify(amqpTemplate).convertAndSend(any(FlagChange.class));
		verify(logger).error(anyString(), any(AmqpException.class));
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
