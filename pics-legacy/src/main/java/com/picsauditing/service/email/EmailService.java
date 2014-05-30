package com.picsauditing.service.email;

import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.email.events.publisher.EmailEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

public class EmailService {

    @Autowired
    private EmailQueueDAO emailQueueDAO;
    private final EmailEventPublisher eventPublisher;
    private final EmailSender emailSender;

    public EmailService(
            EmailEventPublisher publisher,
            EmailSender sender
    ) {
        this.eventPublisher = publisher;
        this.emailSender = sender;
    }

    public void publishEvent(User user, int templateID, EmailStatus status, String reason) {
        eventPublisher.publish(user, templateID, status, reason);
    }

    public void send(EmailQueue email) {
        emailSender.send(email);
    }

    public EmailQueue getQuickbooksError() {
        return emailQueueDAO.getQuickbooksError();
    }
}
