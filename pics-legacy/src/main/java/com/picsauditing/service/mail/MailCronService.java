package com.picsauditing.service.mail;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.subscription.MissingSubscriptionException;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.mail.subscription.SubscriptionException;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.toggle.FeatureToggleCheckerGroovy;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.validator.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public class MailCronService {

	private static final Logger logger = LoggerFactory.getLogger(MailCronService.class);
	public static final String ERROR_INVALID_SUBSCRIPTION = "The subscription is invalid.";
	public static final String ERROR_INVALID_SUBSCRIPTION_ID = "You must supply a valid subscription id.";
	public static final String THE_EMAIL_QUEUE_IS_EMPTY = "The email queue is empty";
	public static final int NUMBER_OF_EMAILS_TO_FIND = 1;
	public static final String SUCCESSFULLY_SENT_EMAILS = "Successfully sent %s out of %s email(s).";
	public static final int SUBSCRIPTIONS_TO_SEND = 15;

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

	public void processEmailSubscription(int subscriptionId, Permissions permissions) throws ValidationException {

		initializeFeatureToggleChecker(permissions);

		if (backProcsEmailSubscriptionsIsDisabled()) {

			if (appPropertyService.emailSubscriptionsAreEnabled()) {
				if (subscriptionId > 0) {
					EmailSubscription emailSubscription = findEmailSubscription(subscriptionId);
					validateEmailSubscription(emailSubscription);
					sendEmailSubscription(emailSubscription);
				}
			}
		}
	}

	public String processPendingEmails(Permissions permissions) {

		initializeFeatureToggleChecker(permissions);
		String statusMessage = "";

		if (backProcsEmailQueueIsDisabled()) {

			List<EmailQueue> pendingEmails = findTheHighestPriorityPendingEmails(NUMBER_OF_EMAILS_TO_FIND);
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

	protected void validateEmailSubscription(EmailSubscription emailSubscription) throws ValidationException {
		if (emailSubscription == null) {
			throw new ValidationException(ERROR_INVALID_SUBSCRIPTION_ID);
		}

		if (emailSubscription.getSubscription() == null) {
			logger.error("validate(): EmailSubscription " + emailSubscription.getId() + " has a null subscription.");
			throw new ValidationException(ERROR_INVALID_SUBSCRIPTION);

		}

		if (emailSubscription.getSubscription() == Subscription.DynamicReports) {
			if (emailSubscription.getReport() == null) {
				logger.error("validate(): EmailSubscription " + emailSubscription.getId() + " for DynamicReports has a null report.");
				throw new ValidationException(ERROR_INVALID_SUBSCRIPTION);
			}
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

	private void initializeFeatureToggleChecker(Permissions permissions) {
		featureToggleChecker.setPermissions(permissions);
	}

	public EmailSubscription findEmailSubscription(int subscriptionId) {
		return subscriptionDAO.find(subscriptionId);
	}

	public List<Integer> findSubscriptionIdsToSend() {
		return subscriptionDAO.findSubscriptionsToSend(SUBSCRIPTIONS_TO_SEND);
	}

	public String getSubscriptionIdsToSendAsCommaDelimited() {
		List<Integer> subscriptionIds = findSubscriptionIdsToSend();
		return (subscriptionIds.isEmpty()) ? "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" : Strings.implode(subscriptionIds);
	}
}
