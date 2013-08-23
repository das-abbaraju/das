package com.picsauditing.messaging;

import com.picsauditing.model.i18n.TranslationLookupData;
import com.picsauditing.model.i18n.TranslationWrapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"PublisherRabbitMqITest-context.xml"})
public class PublisherRabbitMqITest {
    private static final int NUM_TO_CREATE = 50000;

    @Autowired
    @Qualifier("TranslationUsagePublisher")
    private Publisher translationUsagePublisher;
    @Autowired
    private JsonMessageConverter jsonMessageConverter;
    @Autowired
    private RabbitAdmin ampqAdmin;
    @Autowired
    private RandomQueueName uniqueQueueName;
    @Autowired
    private RabbitTemplate amqpTemplate;

    @After
    public void tearDown() {
        ampqAdmin.deleteQueue(uniqueQueueName.queueName);
    }

    @Test
    public void testLargeMessageIsNotTruncatedOnRealQueue() throws Exception {
        List<TranslationLookupData> lookups = createPublishData();
        Message message = jsonMessageConverter.toMessage(lookups, new MessageProperties());
        amqpTemplate.send(message);
        // wait a bit for queue listener to see message and process it
        Thread.sleep(2000);

        Message receivedMessage = amqpTemplate.receive();
        String messageBody = new String(receivedMessage.getBody());
        assertTrue(messageBody.endsWith("]"));
    }

    @Test
    public void testLargeMessageIsNotTruncatedByMessageConverter() throws Exception {
        List<TranslationLookupData> lookups = createPublishData();
        Message message = jsonMessageConverter.toMessage(lookups, new MessageProperties());
        String messageBody = new String(message.getBody());

        assertTrue(messageBody.endsWith("]"));
    }

    private List<TranslationLookupData> createPublishData() {
        List<TranslationLookupData> lookups  = new ArrayList<>();
        for (int i = 0; i < NUM_TO_CREATE; i++) {
            TranslationLookupData data = new TranslationLookupData();
            data.setLocaleRequest("en_US");
            data.setLocaleResponse("en_US");
            data.setReferrer("http://this.is.an.example.com/api/en_US/TestKey");
            data.setRequestDate(new Date());
            data.setPageName("PublisherRabbitMqITest.action");
            data.setMsgKey("PublisherRabbitMqITest.Test.Key");
            data.setEnvironment("TestEnvironment");
            data.setRetrievedByWildcard(false);
            lookups.add(data);
        }
        return lookups;
    }

}
