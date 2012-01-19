package com.picsauditing.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.contractors.ContractorCronStatistics;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class EventSubscriptionBuilder {

	private static EmailSubscriptionDAO subscriptionDAO = (EmailSubscriptionDAO) SpringUtils
			.getBean("EmailSubscriptionDAO");
	private static NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
	private static EmailSenderSpring emailSender = (EmailSenderSpring) SpringUtils.getBean("EmailSenderSpring");

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
			builder.setFromAddress("PICS Customer Service <info@picsauditing.com>");
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

	public static EmailQueue contractorInvoiceEvent(ContractorAccount contractor, Invoice invoice,
			Permissions permissions) throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(45);
		// Adding this to cc Billing until they're confident the billing system is ok
		emailBuilder.setBccAddresses("billing@picsauditing.com");
		emailBuilder.setContractor(contractor, OpPerms.ContractorBilling);
		emailBuilder.addToken("invoice", invoice);
		emailBuilder.addToken("billingUser", contractor.getUsersByRole(OpPerms.ContractorBilling).get(0));
		if (permissions != null)
			emailBuilder.setPermissions(permissions);

		List<String> operatorsString = new ArrayList<String>();

		for (ContractorOperator co : contractor.getNonCorporateOperators()) {
			String doContractorsPay = co.getOperatorAccount().getDoContractorsPay();

			if (doContractorsPay.equals("Yes") || !doContractorsPay.equals("Multiple"))
				operatorsString.add(co.getOperatorAccount().getName());
		}

		Collections.sort(operatorsString);

		emailBuilder.addToken("operators", "Your current list of Operators: " + Strings.implode(operatorsString, ", "));
		emailBuilder.setFromAddress("\"PICS Billing\"<billing@picsauditing.com>");

		EmailQueue email = emailBuilder.build();
		if (invoice.getStatus().isPaid())
			email.setSubject("PICS Payment Receipt for Invoice " + invoice.getId());
		email.setPriority(60);
		email.setHtml(true);
		email.setViewableById(Account.PicsID);
		emailSender.send(email);

		return email;
	}

	public static void theSystemIsDown(ContractorCronStatistics stats) {

		Calendar now = Calendar.getInstance();

		if (stats.isEmailCronError()) {
			List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.EmailCronFailure, 1100);
			for (EmailSubscription subscription : subscriptions) {
				if (subscription.getLastSent() == null || checkLastSentPlusTenMinutes(now, subscription.getLastSent())) {
					sendSystemStatusEmail(subscription, stats);
				}
			}
		}

		if (stats.isContractorCronError()) {
			List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.ContractorCronFailure, 1100);
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
		}
	}

	private static void sendInsuranceEmail(EmailSubscription insuranceSubscription,
			Set<ContractorAudit> expiringPolicies) throws IOException {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.clear();
		emailBuilder.setTemplate(10); // Certificate Expiration
		emailBuilder.setConID(insuranceSubscription.getUser().getAccount().getId());
		emailBuilder.addToken("contractor", insuranceSubscription.getUser().getAccount());
		emailBuilder.addToken("policies", expiringPolicies);
		emailBuilder.setToAddresses(insuranceSubscription.getUser().getEmail());
		EmailQueue email = emailBuilder.build();
		email.setPriority(30);
		email.setViewableById(Account.EVERYONE);
		emailSender.send(email);

		stampNote(insuranceSubscription.getUser().getAccount(), "Sent Policy Expiration Email to "
				+ emailBuilder.getSentTo(), NoteCategory.Insurance);
	}

	private static void sendSystemStatusEmail(EmailSubscription subscription, ContractorCronStatistics stats) {
		EmailBuilder email = new EmailBuilder();
		email.addToken("stats", stats);
		email.setToAddresses(subscription.getUser().getEmail());
		email.setFromAddress("info@picsauditing.com");
		email.setTemplate(subscription.getSubscription().getTemplateID());
		try {
			EmailQueue q = email.build();
			emailSender.sendNow(q);
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
