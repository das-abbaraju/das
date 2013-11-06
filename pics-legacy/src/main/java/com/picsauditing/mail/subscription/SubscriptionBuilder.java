package com.picsauditing.mail.subscription;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;

import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.mail.EmailBuildErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

public abstract class SubscriptionBuilder {
	@Autowired
	private EmailSender sender;
	@Autowired
	private EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	private FeatureToggle featureToggleChecker;
	@Autowired
	private EmailTemplateDAO emailTemplateDAO;

    final static Logger logger = LoggerFactory.getLogger(SubscriptionBuilder.class);


    public void sendSubscription(EmailSubscription subscription) throws IOException, MessagingException, SubscriptionException {
        Map<String, Object> tokens = process(subscription);
        EmailQueue queue;
	    try {
		    queue = buildEmail(subscription, tokens);
	    } catch (EmailBuildErrorException e) {
		    throw new SubscriptionException("Failed to build email for subscription id: " + subscription.getId(), e, subscription.getId());
	    }

        if (queue != null) {
            if (featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)) {
                sender.publishSubscription(queue);
            } else {
                sender.sendNow(queue);
            }

        } else {
            logger.error("SubscriptionBuilder.buildEmail returned null: " +
                    subscription.getSubscription().toString() +
                    " Subscription for " +
                    subscription.getUser().getName() + "(" + subscription.getUser().getId() + ")" +
                    " at " + subscription.getUser().getAccount().getName() + "(" + subscription.getUser().getAccount().getId() + ")"
            );
        }

        subscription.setLastSent(new Date());
        subscriptionDAO.save(subscription);
        tokens.clear();
    }

	public abstract Map<String, Object> process(EmailSubscription subscription) throws IOException;

	private EmailQueue buildEmail(EmailSubscription subscription, Map<String, Object> tokens) throws IOException, EmailBuildErrorException {
		if (tokens.size() > 0) {
			int templateID = subscription.getSubscription().getTemplateID();
			User user = subscription.getUser();

			EmailBuilder emailBuilder = new EmailBuilder();
			EmailTemplate emailTemplate = emailTemplateDAO.find(templateID);
			emailBuilder.setTemplate(emailTemplate);
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			// TODO remove this after we update the templates from username to
			// user.name
			emailBuilder.addToken("username", user.getName());
			emailBuilder.addToken("user", user);

			String seed = "u" + user.getId() + "t" + templateID;

			// TODO: change this in the templates String
			String confirmLink = "http://www.picsorganizer.com/EmailUserUnsubscribe.action?id=" + user.getId()
					+ "&sub=" + subscription.getSubscription() + "&key=" + Strings.hashUrlSafe(seed);

			emailBuilder.addToken("confirmLink", confirmLink);

			emailBuilder.addToken("subscription", subscription);

			emailBuilder.addAllTokens(tokens);
			emailBuilder.setToAddresses(user.getEmail());

			try {
				// If contractor subscription send replies to CSR
				if (user.getAccount().isContractor()) {
					ContractorAccount c = (ContractorAccount) user.getAccount();
					emailBuilder.addToken("contractor", c);
					emailBuilder.setConID(c.getId());
					if (c.getCurrentCsr() != null)
						emailBuilder.setFromAddress("\"" + c.getCurrentCsr().getName() + "\"<" + c.getCurrentCsr().getEmail()
								+ ">");
					// If operator subscription send replies to AM
				} else if (user.getAccount().isOperatorCorporate()) {
					OperatorAccount o = (OperatorAccount) user.getAccount();
					for (AccountUser au : o.getAccountUsers()) {
						if (au.getRole().equals(UserAccountRole.PICSAccountRep))
							emailBuilder.setFromAddress("\"" + au.getUser().getName() + "\"<" + au.getUser().getEmail()
									+ ">");
					}
				}
			} catch (Exception e) {

			}

			EmailQueue email = emailBuilder.build();
			email.setSubjectViewableById(subscription.getSubscription().getSubjectViewableBy());
			email.setBodyViewableById(subscription.getSubscription().getBodyViewableBy());
			email.setCreatedBy(user);

			return email;
		}

		return null;
	}
}
