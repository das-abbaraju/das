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

	private int limit = 2;
	private int subscriptionID = 0;

	@Anonymous
	public String execute() throws Exception {
		// No authentication required since this runs as a cron job
		AppProperty appPropSubEnable = appPropDAO.find("subscription.enable");
		if (Boolean.parseBoolean(appPropSubEnable.getValue())) {
			EmailSubscription emailSubscription = null;
			if (subscriptionID > 0) {
				emailSubscription = subscriptionDAO.find(subscriptionID);
			}

			if (subscriptionID == 0 || emailSubscription == null) {
				addActionError("You must supply a valid subscription id.");
				return SUCCESS;
			}
			try {

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
			} catch (Throwable t) {
				StringWriter sw = new StringWriter();
				t.printStackTrace(new PrintWriter(sw));

				if (!isDebugging()) {
					addActionError("Error occurred on subscription " + subscriptionID + "<br>" + t.getMessage());
					// In case this contractor errored out while running contractor
					// cron
					// we bump the last recalculation date to 1 day in future.
					emailSubscription.setLastSent(DateBean.addDays(emailSubscription.getLastSent(), 1));
					subscriptionDAO.save(emailSubscription);

					StringBuffer body = new StringBuffer();

					body.append("There was an error running MailCron for id=");
					body.append(subscriptionID);
					body.append("\n\n");

					try {
						body.append("Server: " + java.net.InetAddress.getLocalHost().getHostName());
						body.append("\n\n");
					} catch (UnknownHostException e) {
					}

					body.append(t.getStackTrace());

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
	}

	public void setSubscriptionID(int subscriptionID) {
		this.subscriptionID = subscriptionID;
	}

	public int getSubscriptionID() {
		return subscriptionID;
	}

	@Anonymous
	public String listAjax() {
		List<Integer> subs = subscriptionDAO.findSubscriptionsToSend(limit);
		output = Strings.implode(subs);
		return PLAIN_TEXT;
	}

	private void sendMail(String message, int subscriptionID) {
		try {
			SendMail sendMail = new SendMail();
			EmailQueue email = new EmailQueue();
			email.setToAddresses("errors@picsauditing.com");
			email.setFromAddress("PICS Mailer<info@picsauditing.com>");
			email.setSubject("Error in MailCron for subscriptionID = " + subscriptionID);
			email.setBody(message);
			email.setCreationDate(new Date());
			sendMail.send(email);
		} catch (Exception notMuchWeCanDoButLogIt) {
			System.out.println("Error sending email");
			System.out.println(notMuchWeCanDoButLogIt);
			notMuchWeCanDoButLogIt.printStackTrace();
		}
	}
}
