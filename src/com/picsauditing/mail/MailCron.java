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
			if (appPropLimit != null)
				limit = Integer.parseInt(appPropLimit.getValue());

			// DO ALL OPT-IN SUBSCRIPTIONS
			List<EmailSubscription> subs = subscriptionDAO.findSubscriptionsToSend(limit);
			for (EmailSubscription emailSubscription : subs) {
				// TODO: Had to do this because the old Subscription builder in the same package
				com.picsauditing.mail.subscription.SubscriptionBuilder builder = subscriptionFactory
						.getBuilder(emailSubscription.getSubscription());
				builder.process(emailSubscription);

				emailSubscription.setLastSent(new Date());
				subscriptionDAO.save(emailSubscription);
			}

			// DO ALL OPT-OUT SUBSCRIPTIONS (everyone that hasn't opted in or selected none)
			for (Subscription sub : Subscription.values()) {
				if (sub.getNonSubscribedUsersQuery() != null) {
					subs = subscriptionDAO.findOptOutSubscriptionsToSend(sub, limit);

					for (EmailSubscription emailSubscription : subs) {
						com.picsauditing.mail.subscription.SubscriptionBuilder builder = subscriptionFactory
								.getBuilder(sub);
						builder.process(emailSubscription);

						emailSubscription.setLastSent(new Date());
						subscriptionDAO.save(emailSubscription);
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
				this.addActionMessage("Successfully sent " + emails.size() + " email(s)");

		} else {
			/**
			 * Do normal mail. Fallback method in case we have problems with the new method. We can delete this once we
			 * are sure the new subscription system is sending mail as expected.
			 */
			PicsLogger.start("EmailSender");
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
