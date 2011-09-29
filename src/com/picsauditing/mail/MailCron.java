package com.picsauditing.mail;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.util.log.PicsLogger;

/**
 * Run the email task every minute Send 5 emails at a time This translates into 300/hour or 7200/day If this seems slow,
 * it is because of strict limits by gmail
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class MailCron extends PicsActionSupport {

	private int limit;

	@Autowired
	private AppPropertyDAO appPropDAO;
	@Autowired
	private EmailQueueDAO emailQueueDAO;
	@Autowired
	private EmailSubscriptionDAO subscriptionDAO;
	@Autowired
	private SubscriptionBuilderFactory subscriptionFactory;
	@Autowired
	private EmailSenderSpring emailSenderSpring;

	@Anonymous
	public String execute() throws Exception {
		// No authentication required since this runs as a cron job
		AppProperty appPropSubEnable = appPropDAO.find("subscription.enable");
		if (Boolean.parseBoolean(appPropSubEnable.getValue())) {
			AppProperty appPropLimit = appPropDAO.find("subscription.limit");
			if (appPropLimit != null && limit == 0)
				limit = Integer.parseInt(appPropLimit.getValue());

			// DO ALL OPT-IN SUBSCRIPTIONS
			List<EmailSubscription> subs = subscriptionDAO.findSubscriptionsToSend(limit);
			for (EmailSubscription emailSubscription : subs) {
				// TODO: Had to do this because the old Subscription builder in the same package
				com.picsauditing.mail.subscription.SubscriptionBuilder builder = subscriptionFactory
						.getBuilder(emailSubscription.getSubscription());
				if (builder != null) {
					builder.sendSubscription(emailSubscription);
				} else {
					/**
					 * This is if a contractor has an invalid subscription. Notify errors and continue.
					 */
					try {
						EmailQueue email = new EmailQueue();
						email.setToAddresses("errors@picsauditing.com");
						email.setFromAddress("PICS Mailer<info@picsauditing.com>");
						email.setSubject("Error in MailCron for userID = " + emailSubscription.getUser().getId());
						email.setBody("User " + emailSubscription.getUser().getId() + " is subscribed to "
								+ emailSubscription.getSubscription() + " on a " + emailSubscription.getTimePeriod()
								+ " time period. There is no mapping for this Subscription "
								+ "in the SubscriptionFactory.");
						email.setCreationDate(new Date());
						emailSenderSpring.send(email);
					} catch (Exception notMuchWeCanDoButLogIt) {
						System.out.println("Error sending email");
						System.out.println(notMuchWeCanDoButLogIt);
						notMuchWeCanDoButLogIt.printStackTrace();
					}
				}
			}

			/**
			 * Do normal mail NOTE: This is a copy of the else block using new spring loaded Email Sender.
			 */
			List<EmailQueue> emails = emailQueueDAO.getPendingEmails(limit);
			if (emails.size() == 0) {
				addActionMessage("The email queue is empty");
				return ACTION_MESSAGES;
			}

			for (EmailQueue email : emails) {
				try {
					emailSenderSpring.sendNow(email);
				} catch (Exception e) {
					PicsLogger.log("ERROR with MailCron: " + e.getMessage());
					addActionError("Failed to send email: " + e.getMessage());
				}
			}
			if (this.getActionErrors().size() == 0)
				this.addActionMessage("Successfully sent " + (emails.size() + subs.size()) + " email(s)");

		} else {
			/**
			 * Do normal mail. Fallback method in case we have problems with the new method. We can delete this once we
			 * are sure the new subscription system is sending mail as expected.
			 */
			PicsLogger.start("EmailSender");
			AppProperty appPropLimit = appPropDAO.find("subscription.limit");
			if (appPropLimit != null && limit == 0)
				limit = Integer.parseInt(appPropLimit.getValue());

			try {
				List<EmailQueue> emails = emailQueueDAO.getPendingEmails(limit);
				if (emails.size() == 0) {
					addActionMessage("The email queue is empty");
					return ACTION_MESSAGES;
				}

				// Get the default sender (info@pics)
				EmailSender sender = new EmailSender();
				for (EmailQueue email : emails) {
					try {
						sender.sendNow(email);
					} catch (Exception e) {
						PicsLogger.log("ERROR with MailCron: " + e.getMessage());
						addActionError("Failed to send email: " + e.getMessage());
					}
				}
				if (this.getActionErrors().size() == 0)
					this.addActionMessage("Successfully sent " + emails.size() + " email(s)");

			} finally {
				PicsLogger.stop();
			}
		}

		return ACTION_MESSAGES;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
