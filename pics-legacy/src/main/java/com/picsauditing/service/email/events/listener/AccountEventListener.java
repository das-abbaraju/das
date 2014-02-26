package com.picsauditing.service.email.events.listener;

import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailException;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.service.account.events.ContractorEvent;
import com.picsauditing.service.account.events.ContractorEventType;
import com.picsauditing.service.account.events.SpringContractorEvent;
import com.picsauditing.service.email.EmailService;
import com.picsauditing.service.email.events.publisher.EmailEventPublisher;
import com.picsauditing.service.email.logging.EmailLog;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

public class AccountEventListener implements ApplicationListener<SpringContractorEvent> {

    @Autowired
    private EmailService emailService;
    @Autowired
    private FeatureToggleCheckerGroovy featureToggleChecker;
    @Autowired
    private EmailLog logger;

    public void onApplicationEvent(SpringContractorEvent event) {
        ContractorAccount account = event.account;
        if (event.event == ContractorEventType.Registration) {
            if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_SUPPRESS_WELCOME_EMAILS)) {
                sendWelcomeEmail(account);
                emailService.publishEvent(
                        event.account.getUsers().get(0),
                        EmailTemplate.WELCOME_EMAIL_TEMPLATE,
                        EmailStatus.Sent,
                        "Welcome Email Sent"
                );
//                addNote(contractor, "Welcome Email Sent");
            } else {
                addNote(contractor, "Welcome Email NOT Sent");
            }

        }
    }

    protected void sendWelcomeEmail(ContractorAccount contractor) throws EmailException, UnsupportedEncodingException, IOException {
        User user = contractor.getUsers().get(0);
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(EmailTemplate.WELCOME_EMAIL_TEMPLATE);
		emailBuilder.setUser(user);
		emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
		// TODO move to EncodedKey
		user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
		String confirmLink = "http://www.picsorganizer.com/Login.action?username="
				+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
		emailBuilder.addToken("confirmLink", confirmLink);
		emailBuilder.addToken("contactName", user.getName());
		emailBuilder.addToken("userName", user.getUsername());

		EmailQueue emailQueue = null;
		try {
			emailQueue = emailBuilder.build();
		} catch (EmailBuildErrorException e) {
			logger.error("sendWelcomeEmail(): Failed to send build email for user with id: {}. {}",
					new Object[]{user.getId(), e.getMessage()});
		}
		emailQueue.setHtml(true);
		emailQueue.setVeryHighPriority();
		emailQueue.setSubjectViewableById(Account.EVERYONE);
		emailQueue.setBodyViewableById(Account.EVERYONE);
		emailService.send(emailQueue);
	}
}
