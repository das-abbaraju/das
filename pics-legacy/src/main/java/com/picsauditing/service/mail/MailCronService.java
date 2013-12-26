package com.picsauditing.service.mail;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.subscription.*;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public class MailCronService {

	private static final Logger logger = LoggerFactory.getLogger(MailCronService.class);
	public static final String ERROR_NO_EMAIL_SUBSCRIPTION_FOUND_FOR_ID = "No email subscription found for id %s";
	public static final String ERROR_EMAIL_SUBSCRIPTION_HAS_A_NULL_USER = "EmailSubscription %s has a null user (recipient).";
	public static final String ERROR_EMAIL_SUBSCRIPTION_HAS_AN_EMPTY_USER_EMAIL = "EmailSubscription %s has a null or blank user(recipient) email.";
	public static final String ERROR_EMAIL_SUBSCRIPTION_HAS_A_NULL_SUBSCRIPTION = "EmailSubscription %s has a null subscription.";
	public static final String ERROR_EMAIL_SUBSCRIPTION_FOR_DYNAMIC_REPORTS_HAS_A_NULL_REPORT = "EmailSubscription %s for DynamicReports has a null report.";
	public static final String THE_EMAIL_QUEUE_IS_EMPTY = "The email queue is empty";
	public static final int NUMBER_OF_EMAILS_TO_FIND_DEFAULT = 5;
	public static final String SUCCESSFULLY_SENT_EMAILS = "Successfully sent %s out of %s email(s).";
	public static final int SUBSCRIPTIONS_TO_SEND_DEFAULT = 15;
    static final String SUBSCRIPTION_ENABLE = "subscription.enable";
    static final String EMAIL_QUEUE_SENDING_LIMIT_TOGGLE_NAME = "email_queue.sending_limit";

	@Autowired
	private AppPropertyService appPropertyService;
	@Autowired
	private SubscriptionBuilderFactory subscriptionFactory;
	@Autowired
	private EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private FeatureToggleCheckerGroovy featureToggleChecker;

	public void processEmailSubscription(int subscriptionId) throws SubscriptionValidationException {
		if (backProcsEmailSubscriptionsIsDisabled()) {

			if (appPropertyService.isEnabled(SUBSCRIPTION_ENABLE, true)) {
				if (subscriptionId > 0) {
					EmailSubscription emailSubscription = findEmailSubscription(subscriptionId);
					validateEmailSubscription(emailSubscription, subscriptionId);
					sendEmailSubscription(emailSubscription);
				}
			}
		}
	}

	public String processPendingEmails() {
		String statusMessage = "";

		if (backProcsEmailQueueIsDisabled()) {

            int emailsToFindLimit = appPropertyService.getPropertyInt(EMAIL_QUEUE_SENDING_LIMIT_TOGGLE_NAME, NUMBER_OF_EMAILS_TO_FIND_DEFAULT);
			List<EmailQueue> pendingEmails = findTheHighestPriorityPendingEmails(emailsToFindLimit);
			if (pendingEmails.size() == 0) {
				statusMessage = THE_EMAIL_QUEUE_IS_EMPTY;
				return statusMessage;
			}

			int successfulCount = 0;
			for (EmailQueue email : pendingEmails) {
				try {
					emailSender.sendNow(email);
					successfulCount += 1;

				} catch (MessagingException e) {
					logger.error("processPendingEmails(): Failed to send email with id: {}. {}",
							new Object[]{email.getId(), e.getMessage()});
					processEmailForSendError(email);
				}
			}
			statusMessage = String.format(SUCCESSFULLY_SENT_EMAILS, successfulCount, pendingEmails.size());

		}

		return statusMessage;
	}

	private boolean backProcsEmailSubscriptionsIsDisabled() {
		return !featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL);
	}

	protected void validateEmailSubscription(EmailSubscription emailSubscription, int subscriptionId) throws SubscriptionValidationException {
		if (emailSubscription == null) {
			String message = String.format(ERROR_NO_EMAIL_SUBSCRIPTION_FOUND_FOR_ID, subscriptionId);
			throw new SubscriptionValidationException(message, subscriptionId);
		}

		if (emailSubscription.getSubscription() == null) {
			String message = String.format(ERROR_EMAIL_SUBSCRIPTION_HAS_A_NULL_SUBSCRIPTION, subscriptionId);
			throw new SubscriptionValidationException(message, subscriptionId);
		}

		if (emailSubscription.getSubscription() == Subscription.DynamicReports) {
			if (emailSubscription.getReport() == null) {
				String message = String.format(ERROR_EMAIL_SUBSCRIPTION_FOR_DYNAMIC_REPORTS_HAS_A_NULL_REPORT, subscriptionId);
				throw new SubscriptionValidationException(message, subscriptionId);
			}
		}

		if (emailSubscription.getUser() == null) {
			String message = String.format(ERROR_EMAIL_SUBSCRIPTION_HAS_A_NULL_USER, subscriptionId);
			throw new SubscriptionValidationException(message, subscriptionId);
		}

		if (StringUtils.isEmpty(emailSubscription.getUser().getEmail())) {
			String message = String.format(ERROR_EMAIL_SUBSCRIPTION_HAS_AN_EMPTY_USER_EMAIL, subscriptionId);
			throw new SubscriptionValidationException(message, subscriptionId);
		}
	}

	private void setSubscriptionToBeReprocessedTomorrow(EmailSubscription emailSubscription) {
		try {
			emailSubscription.setLastSent(DateBean.addDays(emailSubscription.getLastSent(), 1));
			subscriptionDAO.save(emailSubscription);
		} catch (Exception e) {
			logger.error("setSubscriptionToBeReprocessedTomorrow(): Failed to reschedule emailSubscription with id: {}. {}",
					new Object[]{emailSubscription.getId(), e.getMessage()});
		}
	}

	private void sendEmailSubscription(EmailSubscription emailSubscription) {
		SubscriptionBuilder builder = getSubscriptionBuilder(emailSubscription);
		sendEmailSubscription(emailSubscription, builder);
	}

	private SubscriptionBuilder getSubscriptionBuilder(EmailSubscription emailSubscription) {
		SubscriptionBuilder builder = null;
		try {
			builder = subscriptionFactory.getBuilder(emailSubscription.getSubscription());
		} catch (MissingSubscriptionException e) {
			logger.error("getSubscriptionBuilder(): Could not find a builder for emailSubscription with id: {}, subscription: {}. {}",
					new Object[]{emailSubscription.getId(), emailSubscription.getSubscription(), e.getMessage()});
		}
		return builder;
	}

	protected void sendEmailSubscription(EmailSubscription emailSubscription, SubscriptionBuilder builder) {
		try {
			builder.sendSubscription(emailSubscription);
		} catch (IOException | MessagingException e) {
			logger.error("sendEmailSubscription(): Failed to send subscription for emailSubscription with id: {}. {}",
					new Object[]{emailSubscription.getId(), e.getMessage()});
			setSubscriptionToBeReprocessedTomorrow(emailSubscription);
		} catch (SubscriptionException e) {
			logger.error("sendEmailSubscription(): Failed to send a subscription", e);
		}
	}

	private boolean backProcsEmailQueueIsDisabled() {
		return !featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE);
	}

	private List<EmailQueue> findTheHighestPriorityPendingEmails(int limit) {
		return emailQueueDAO.getPendingEmails(limit);
	}

	private void processEmailForSendError(EmailQueue email) {
		email.setStatus(EmailStatus.Error);
		if (Strings.isEmpty(email.getToAddresses())) {
			email.setToAddresses(EmailAddressUtils.PICS_ERROR_EMAIL_ADDRESS);
		}

		try {
			emailSender.send(email);
		} catch (Exception e) {
			logger.error("processEmailSendError(): Failed to send error-email with id: {}. {}",
					new Object[]{email.getId(), e.getMessage()});
		}
	}

	public EmailSubscription findEmailSubscription(int subscriptionId) {
		return subscriptionDAO.find(subscriptionId);
	}

	private List<Integer> findSubscriptionIdsToSend() {
        String runtimeWhere = appPropertyService.getPropertyString("subscription.whereClause", "");
        int limit = appPropertyService.getPropertyInt("subscription.limit", SUBSCRIPTIONS_TO_SEND_DEFAULT);
		return subscriptionDAO.findSubscriptionsToSend(runtimeWhere, limit);
	}

	public String getSubscriptionIdsToSendAsCommaDelimited() {
		List<Integer> subscriptionIds = findSubscriptionIdsToSend();
        // TODO look at the python script to see if it can accept "0" or "0,0" or only 15 zeros
		return (subscriptionIds.isEmpty()) ? "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" : Strings.implode(subscriptionIds);
	}
}
