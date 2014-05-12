package com.picsauditing.flagcalculator.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class FlagChangePublisher {
    private RabbitTemplate amqpTemplate;

    public void publish(Object message) {
        amqpTemplate.convertAndSend(message);
    }

    public void setAmqpTemplate(RabbitTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }
}
