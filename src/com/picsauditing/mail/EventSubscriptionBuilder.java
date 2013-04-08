package com.picsauditing.mail;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.contractors.ContractorCronStatistics;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.messaging.Publisher;
import com.picsauditing.toggle.FeatureToggle;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class EventSubscriptionBuilder {

	private static Publisher emailRequestPublisher = SpringUtils.getBean(SpringUtils.EMAIL_REQUEST_PUBLISHER);
	private static EmailReportRunner runner = SpringUtils.getBean(SpringUtils.EMAIL_REPORT_RUNNER);
	private static FeatureToggle featureToggle = SpringUtils.getBean(SpringUtils.FEATURE_TOGGLE);
	private static EmailSubscriptionDAO subscriptionDAO = SpringUtils.getBean(SpringUtils.EMAIL_SUBSCRIPTION_DAO);
	private static NoteDAO noteDAO = SpringUtils.getBean(SpringUtils.NOTE_DAO);
	private static EmailSender emailSender = SpringUtils.getBean(SpringUtils.EMAIL_SENDER);

	private static final Logger logger = LoggerFactory.getLogger(EventSubscriptionBuilder.class);

	public static final Integer PICS_CONTRACTOR_INVOICE_TEMPLATE_ID = 45;

	// for test injection only
	private static EmailBuilder emailBuilder = null;

	public static void contractorFinishedEvent(EmailSubscriptionDAO subscriptionDAO, ContractorOperator co)
			throws Exception {
		Date now = new Date();
		final int templateID = 63;
		final String serverName = "http://www.picsorganizer.com/";

		List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.ContractorFinished,
				SubscriptionTimePeriod.Event, co.getOperatorAccount().getId());

		for (EmailSubscription subscription : subscriptions) {
			EmailBuilder builder = new EmailBuilder();
			builder.setTemplate(templateID);
			builder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			builder.addToken("contractor", co.getContractorAccount());
			builder.addToken("operator", co.getOperatorAccount());
			String seed = "u" + subscription.getUser().getId() + "t" + templateID;
			String confirmLink = serverName + "EmailUserUnsubscribe.action?id=" + subscription.getUser().getId()
					+ "&sub=" + subscription + "&key=" + Strings.hashUrlSafe(seed);
			builder.addToken("confirmLink", confirmLink);
			builder.setUser(subscription.getUser());
			EmailQueue q = builder.build();
			q.setHtml(true);
			q.setViewableBy(co.getOperatorAccount().getTopAccount());

			emailSender.send(q);

			subscription.setLastSent(now);
			subscriptionDAO.save(subscription);
		}
	}

	public static EmailQueue contractorInvoiceEvent(ContractorAccount contractor, Invoice invoice) throws EmailException, IOException {
		EmailQueue email = buildInvoiceEmailQueueObject(contractor, invoice);

		if (com.picsauditing.model.l10n.Invoice.invoiceIsToBeEmailedViaBPROCS(contractor)) {
			sendInvoiceEmailViaBProcs(invoice);
		} else {
			emailSender.send(email);
		}

		return email;
	}

	private static void sendInvoiceEmailViaBProcs(Invoice invoice) {
		EmailRequestDTO request = runner.buildEmailRequest();
		request.templateID = PICS_CONTRACTOR_INVOICE_TEMPLATE_ID;
		request.invoiceIDs.add(invoice.getId());
		emailRequestPublisher.publish(request);
	}

	private static EmailQueue buildInvoiceEmailQueueObject(ContractorAccount contractor, Invoice invoice) throws EmailException, IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(PICS_CONTRACTOR_INVOICE_TEMPLATE_ID);
		// Adding this to cc Billing until they're confident the billing system
		// is ok
		emailBuilder.setBccAddresses(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));
		emailBuilder.setContractor(contractor, OpPerms.ContractorBilling);
		emailBuilder.addToken("invoice", invoice);
		emailBuilder.addToken("user", contractor.getUsersByRole(OpPerms.ContractorBilling).get(0));

		List<String> operatorsString = new ArrayList<String>();

		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			String doContractorsPay = co.getOperatorAccount().getDoContractorsPay();

			if (doContractorsPay.equals("Yes") || !doContractorsPay.equals("Multiple")) {
				operatorsString.add(co.getOperatorAccount().getName());
			}
		}

		Collections.sort(operatorsString);

		emailBuilder.addToken("operators", Strings.implode(operatorsString, ", "));

		emailBuilder.setFromAddress(EmailAddressUtils.getBillingEmail(contractor.getCurrency()));

		EmailQueue email = emailBuilder.build();
		email.setHighPriority();
		email.setHtml(true);
		email.setViewableById(Account.PicsID);
		return email;
	}

	public static void theSystemIsDown(ContractorCronStatistics stats) {

		Calendar now = Calendar.getInstance();

		if (stats.isEmailCronError()) {
			List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.EmailCronFailure, Account.PicsID);
			for (EmailSubscription subscription : subscriptions) {
				if (subscription.getLastSent() == null || checkLastSentPlusTenMinutes(now, subscription.getLastSent())) {
					sendSystemStatusEmail(subscription, stats);
				}
			}
		}

		if (stats.isContractorCronError()) {
			List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.ContractorCronFailure,
					Account.PicsID);
			if (stats.isContractorCronError()) {
				for (EmailSubscription subscription : subscriptions) {
					if (subscription.getLastSent() == null
							|| checkLastSentPlusTenMinutes(now, subscription.getLastSent())) {
						sendSystemStatusEmail(subscription, stats);
					}
				}
			}
		}
	}

	private static boolean checkLastSentPlusTenMinutes(Calendar now, Date lastSent) {
		// Make sure this email hasn't been sent within the last 10 minutes
		Calendar result = Calendar.getInstance();
		result.setTime(lastSent);
		result.add(Calendar.MINUTE, 10);
		return now.after(result);
	}

	public static void sendExpiringCertificatesEmail(Set<EmailSubscription> contractorInsuranceSubscriptions,
													 Set<ContractorAudit> expiringPolicies) throws EmailException, IOException {
		for (EmailSubscription contractorInsuranceSubscription : contractorInsuranceSubscriptions) {
			sendInsuranceEmail(contractorInsuranceSubscription, expiringPolicies);
			contractorInsuranceSubscription.setLastSent(new Date());
			subscriptionDAO.save(contractorInsuranceSubscription);
		}
	}

	public static void pqfSubmittedForCao(ContractorAuditOperator cao) {

	}

	public static void notifyUpcomingImplementationAudit(ContractorAudit audit) throws NoUsersDefinedException,
			IOException {
		EmailBuilder emailBuilder = emailBuilder();
		emailBuilder.clear();
		emailBuilder.setTemplate(247);
		emailBuilder.setConID(audit.getContractorAccount().getId());
		emailBuilder.addToken("contractor", audit.getContractorAccount());
		emailBuilder.setToAddresses(audit.getContractorAccount().getActiveUser().getEmail());
		emailBuilder.setUser(audit.getContractorAccount().getActiveUser());
		emailBuilder.setFromAddress(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS);
		try {
			EmailQueue email = emailBuilder.build();
			email.setLowPriority();
			email.setViewableById(Account.EVERYONE);
			emailSender.send(email);

			stampNote(audit.getContractorAccount(),
					"Sent 1 week prior audit notice Email to " + emailBuilder.getSentTo(), NoteCategory.Audits);
		} catch (Exception e) {
			sendInvalidContractorAccountEmail(audit);
		}
	}

	private static EmailBuilder emailBuilder() {
		if (emailBuilder == null) {
			return new EmailBuilder();
		} else {
			return emailBuilder;
		}
	}

	private static void sendInvalidContractorAccountEmail(ContractorAudit audit) {
		try {
			EmailQueue email = new EmailQueue();
			email.setToAddresses(EmailAddressUtils.PICS_AUDIT_EMAIL_ADDRESS);
			email.setContractorAccount(audit.getContractorAccount());
			email.setSubject("Contractor Missing Email Address");
			email.setBody(audit.getContractorAccount().getName() + " (" + audit.getContractorAccount().getId()
					+ ") has no valid email address. "
					+ "The system is unable to send automated emails to this account. "
					+ "Attempted to send 1 week prior audit implementation email.");
			email.setLowPriority();
			email.setViewableById(Account.PicsID);
			emailSender.send(email);
			stampNote(email.getContractorAccount(),
					"Failed to send Audit Notification because of no valid email address.", NoteCategory.Audits);
		} catch (Exception e) {
			logger.error("Error while sending invalid email address email to auditors.", e);
		}

	}

	private static void sendInsuranceEmail(EmailSubscription insuranceSubscription,
										   Set<ContractorAudit> expiringPolicies) throws IOException {

		User user = insuranceSubscription.getUser();

		if (!user.getAccount().isContractor() || expiringPolicies.isEmpty()) return;

		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.clear();
		emailBuilder.setTemplate(10); // Certificate Expiration
		emailBuilder.setConID(user.getAccount().getId());
		emailBuilder.addToken("contractor", user.getAccount());
		emailBuilder.addToken("policies", expiringPolicies);
		emailBuilder.setToAddresses(user.getEmail());
		String seed = "u" + user.getId() + "t" + insuranceSubscription.getSubscription().getTemplateID();
		String confirmLink = "http://www.picsorganizer.com/EmailUserUnsubscribe.action?id=" + user.getId() + "&sub="
				+ insuranceSubscription.getSubscription() + "&key=" + Strings.hashUrlSafe(seed);
		emailBuilder.addToken("confirmLink", confirmLink);
		emailBuilder.setUser(user);

		Account account = user.getAccount();
		if (account.isContractor() && ((ContractorAccount) account).getCurrentCsr() != null) {
			emailBuilder.setFromAddressAsCSRFor((ContractorAccount) account);
		}

		EmailQueue email = emailBuilder.build();
		email.setLowPriority();
		email.setViewableById(Account.EVERYONE);
		emailSender.send(email);

		stampNote(user.getAccount(), "Sent Policy Expiration Email to " + emailBuilder.getSentTo(),
				NoteCategory.Insurance);
	}

	private static void sendSystemStatusEmail(EmailSubscription subscription, ContractorCronStatistics stats) {
		EmailBuilder email = new EmailBuilder();
		email.addToken("stats", stats);
		email.setToAddresses(subscription.getUser().getEmail());
		email.setFromAddress(EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS);
		email.setTemplate(subscription.getSubscription().getTemplateID());
		try {
			EmailQueue q = email.build();
			emailSender.send(q);
			subscription.setLastSent(new Date());
			subscriptionDAO.save(subscription);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void stampNote(Account account, String text, NoteCategory noteCategory) {
		Note note = new Note(account, new User(User.SYSTEM), text);
		note.setCanContractorView(true);
		note.setPriority(LowMedHigh.High);
		note.setNoteCategory(noteCategory);
		note.setAuditColumns(new User(User.SYSTEM));
		note.setViewableById(Account.PicsID);
		noteDAO.save(note);
	}
}
