package com.picsauditing.mail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.mail.subscription.MissingSubscriptionException;
import com.picsauditing.mail.subscription.SubscriptionBuilder;
import com.picsauditing.mail.subscription.SubscriptionBuilderFactory;
import com.picsauditing.util.Strings;
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

	private int subscriptionID = 0;

	@Anonymous
	public String execute() throws Exception {
		/**
		 * Process Email Subscription
		 */
		AppProperty enableSubscriptions = appPropDAO.find("subscription.enable");
		if (Boolean.parseBoolean(enableSubscriptions.getValue())) {
			EmailSubscription emailSubscription = null;
			if (subscriptionID > 0) {
				emailSubscription = subscriptionDAO.find(subscriptionID);

				if (emailSubscription == null) {
					addActionError("You must supply a valid subscription id.");
					return ACTION_MESSAGES;
				}

				try {
					SubscriptionBuilder builder = subscriptionFactory.getBuilder(emailSubscription.getSubscription());
					builder.sendSubscription(emailSubscription);
				} catch (MissingSubscriptionException e) {
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
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));

					if (!isDebugging()) {
						addActionError("Error occurred on subscription " + subscriptionID + "<br>" + e.getMessage());
						// In case this contractor errored out while running contractor
						// cron we bump the last recalculation date to 1 day in future.
						emailSubscription.setLastSent(DateBean.addDays(emailSubscription.getLastSent(), 1));
						subscriptionDAO.save(emailSubscription);

						StringBuffer body = new StringBuffer();

						body.append("There was an error running MailCron for id=");
						body.append(subscriptionID);
						body.append("\n\n");

						try {
							body.append("Server: " + java.net.InetAddress.getLocalHost().getHostName());
							body.append("\n\n");
						} catch (UnknownHostException uh) {
						}

						body.append(e.getStackTrace());

						body.append(sw.toString());

						try {
							sendMail(body.toString(), subscriptionID);
						} catch (Exception notMuchWeCanDoButLogIt) {
							System.out.println("Error sending email");
							System.out.println(notMuchWeCanDoButLogIt);
							notMuchWeCanDoButLogIt.printStackTrace();
						}
					} else {
						addActionError(sw.toString());
					}
				}
			}
		}

		/**
		 * Send mail
		 */
		List<EmailQueue> emails = emailQueueDAO.getPendingEmails(1);
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

		return ACTION_MESSAGES;
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
		output = (subs.isEmpty()) ? "0" : Strings.implode(subs);
		return PLAIN_TEXT;
	}

	private void sendMail(String message, int subscriptionID) {
		try {
			GridSender gridSender = new GridSender();
			EmailQueue email = new EmailQueue();
			email.setToAddresses("errors@picsauditing.com");
			email.setFromAddress("PICS Mailer<info@picsauditing.com>");
			email.setSubject("Error in MailCron for subscriptionID = " + subscriptionID);
			email.setBody(message);
			email.setCreationDate(new Date());
			gridSender.sendMail(email);
		} catch (Exception notMuchWeCanDoButLogIt) {
			System.out.println("Error sending email");
			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}
	}
}
