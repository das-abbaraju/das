package com.picsauditing.model.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class TranslationUsageListener implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(TranslationUsageListener.class);

    @Autowired
    private TranslationLogger translationLogger;
    @Autowired
    private MessageConverter messageConverter;

    @Override
    public void onMessage(Message message) {
        try {
            TranslationLookupData lookupData = (TranslationLookupData) messageConverter.fromMessage(message);
            translationLogger.handle(lookupData);
        } catch (Exception e) {
            logger.error("Unable to handle translation usage message ({}): {}", new String(message.getBody()), e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Unable to handle translation usage message", e);
        }
    }
}
