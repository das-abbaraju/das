package com.picsauditing.model.i18n;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.mockito.Mockito.*;



public class TranslationUsageListenerTest {
    private TranslationUsageListener translationUsageListener;

    @Mock
    private TranslationLogger translationLogger;
    @Mock
    private MessageConverter messageConverter;
    @Mock
    private Message message;
    @Mock
    private TranslationLookupData lookupData;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationUsageListener = new TranslationUsageListener();

        when(message.getBody()).thenReturn("testing".getBytes());

        Whitebox.setInternalState(translationUsageListener, "translationLogger", translationLogger);
        Whitebox.setInternalState(translationUsageListener, "messageConverter", messageConverter);
    }

    @Test
    public void testOnMessage_Happy() throws Exception {
        when(messageConverter.fromMessage(message)).thenReturn(lookupData);
        translationUsageListener.onMessage(message);
        verify(translationLogger).handle(lookupData);
    }

    @Test(expected = AmqpRejectAndDontRequeueException.class)
    public void testOnMessage_ConversionExceptionThrowsAmqpRejectAndDontRequeueException() throws Exception {
        doThrow(new MessageConversionException("testing")).when(messageConverter).fromMessage(any(Message.class));
        translationUsageListener.onMessage(message);
    }

    @Test(expected = AmqpRejectAndDontRequeueException.class)
    public void testOnMessage_LoggerExceptionThrowsAmqpRejectAndDontRequeueException() throws Exception {
        doThrow(new MessageConversionException("testing")).when(translationLogger).handle(any(TranslationLookupData.class));
        translationUsageListener.onMessage(message);
    }

}
