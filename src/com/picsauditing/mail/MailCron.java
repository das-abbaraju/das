package com.picsauditing.mail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailStatus;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

/**
 * Run the email task every minute Send 5 emails at a time This translates into 300/hour or 7200/day If this seems slow,
 * it is because of strict limits by gmail
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class MailCron extends PicsActionSupport {

	public static final String ERROR_INVALID_SUBSCRIPTION = "The subscription is invalid.";
	public static final String ERROR_INVALID_SUBSCRIPTION_ID = "You must supply a valid subscription id.";
	@Autowired
	private AppPropertyDAO appPropDAO;
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	@Autowired
	private EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	private SubscriptionBuilderFactory subscriptionFactory;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private FeatureToggle featureToggleChecker;

	private int subscriptionID = 0;

	private final Logger logger = LoggerFactory.getLogger(MailCron.class);
	
	@Anonymous
	public String execute() throws Exception {
		/**
		 * Process Email Subscription
		 */
		if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_SUBSCRIPTIONEMAIL)) {
			AppProperty enableSubscriptions = appPropDAO.find("subscription.enable");
			if (Boolean.parseBoolean(enableSubscriptions.getValue())) {
				if (subscriptionID > 0) {
					EmailSubscription emailSubscription = subscriptionDAO.find(subscriptionID);
	
					validate(emailSubscription);
					if (hasActionErrors()) {
						return ACTION_MESSAGES;
					}

					try {
						SubscriptionBuilder builder = subscriptionFactory.getBuilder(emailSubscription.getSubscription());
						builder.sendSubscription(emailSubscription);
					} catch (Exception continueUpTheStack) {
						setSubscriptionToBeReprocessedTomorrow(emailSubscription);
	
						throw continueUpTheStack;
					}
				}
			}
		}
		
		if (!featureToggleChecker.isFeatureEnabled(FeatureToggle.TOGGLE_BPROC_EMAILQUEUE)) {
			// send email from the email_queue
			List<EmailQueue> emails = emailQueueDAO.getPendingEmails(1);
			if (emails.size() == 0) {
				addActionMessage("The email queue is empty");
				return ACTION_MESSAGES;
			}
	
			try {
				for (EmailQueue email : emails) {
					emailSender.sendNow(email);
				}
	
				if (getActionErrors().size() == 0)
					addActionMessage("Successfully sent " + emails.size() + " email(s)");
	
			} catch (Exception continueUpTheStack) {
				changeEmailStatusToError(emails);
	
				throw continueUpTheStack;
			}
		}
		
		return ACTION_MESSAGES;
	}

	protected void validate(EmailSubscription emailSubscription) {
		if (emailSubscription == null) {
			addActionError(ERROR_INVALID_SUBSCRIPTION_ID);
			return;
		}

		if (emailSubscription.getSubscription() == null) {
			addActionError(ERROR_INVALID_SUBSCRIPTION);
			logger.error("MailCron.validate: EmailSubscription " + emailSubscription.getId() + " has a null subscription.");
			return;
		}

		if (emailSubscription.getSubscription() == Subscription.DynamicReports) {
			if (emailSubscription.getReport() == null) {
				addActionError(ERROR_INVALID_SUBSCRIPTION);
				logger.error("MailCron.validate: EmailSubscription " + emailSubscription.getId() + " for DynamicReports has a null report.");
				return;
			}
		}
	}

	private void setSubscriptionToBeReprocessedTomorrow(EmailSubscription emailSubscription) {
		try {
			emailSubscription.setLastSent(DateBean.addDays(emailSubscription.getLastSent(), 1));
			subscriptionDAO.save(emailSubscription);
		} catch (Exception notMuchWeCanDoButLogIt) {
			logger.error("Error processing subscription {}", emailSubscription.getId());
			logger.error(notMuchWeCanDoButLogIt.getMessage());
		}
	}

	private void changeEmailStatusToError(List<EmailQueue> emails) {
		try {
			for (EmailQueue email : emails) {
				email.setStatus(EmailStatus.Error);
				if (Strings.isEmpty(email.getToAddresses()))
					email.setToAddresses(EmailAddressUtils.PICS_ERROR_EMAIL_ADDRESS);
				emailSender.send(email);
			}
		} catch (Exception notMuchWeCanDoButLogIt) {
			logger.error("Error sending email");
			logger.error(notMuchWeCanDoButLogIt.getMessage());
		}
	}

	public void setSubscriptionID(int subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public int getSubscriptionID() {
		return subscriptionID;
	}

	@Anonymous
	public String listAjax() {
		List<Integer> subs = subscriptionDAO.findSubscriptionsToSend(15);
		output = (subs.isEmpty()) ? "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0" : Strings.implode(subs);
		return PLAIN_TEXT;
	}

}
