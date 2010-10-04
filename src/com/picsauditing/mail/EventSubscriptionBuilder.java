package com.picsauditing.mail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class EventSubscriptionBuilder {

	public static void contractorFinishedEvent(EmailSubscriptionDAO subscriptionDAO, ContractorOperator co)
			throws Exception {
		Date now = new Date();
		final int templateID = 63;
		final String serverName = "http://www.picsorganizer.com/";

		List<EmailSubscription> subscriptions = subscriptionDAO.find(Subscription.ContractorFinished,
				SubscriptionTimePeriod.Event, co.getOperatorAccount().getId());

		OperatorAccount parent = co.getOperatorAccount().getParent();
		while(parent != null){ // adding corporate subscriptions
			subscriptions.addAll(subscriptionDAO.find(Subscription.ContractorFinished,
					SubscriptionTimePeriod.Event, parent.getId()));
			parent = parent.getParent();
		}
		
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

			EmailSender.send(q);

			subscription.setLastSent(now);
			subscriptionDAO.save(subscription);
		}
	}

	public static EmailQueue contractorInvoiceEvent(ContractorAccount contractor, Invoice invoice,
			Permissions permissions) throws Exception {
		EmailBuilder emailBuilder = new EmailBuilder();
		// creating list of recipients
		Set<String> billingUserEmails = new HashSet<String>();
		for(User u : contractor.getUsersByRole(OpPerms.ContractorBilling))
			billingUserEmails.add(u.getEmail());
		// removing main recipient
		billingUserEmails.remove(contractor.getUsersByRole(OpPerms.ContractorBilling).get(0).getEmail());
		String emails = Strings.implode(billingUserEmails, ", ");
		if(!Strings.isEmpty(emails))
			emailBuilder.setCcAddresses(emails);
		// finishing rest of email
		emailBuilder.setTemplate(45);
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

		List<String> emailAddresses = new ArrayList<String>();

		if (contractor.getPaymentMethod().isCreditCard()) {
			if (!Strings.isEmpty(contractor.getCcEmail()))
				emailAddresses.add(contractor.getCcEmail());
		}
		User billing = contractor.getUsersByRole(OpPerms.ContractorBilling).get(0);
		if (!Strings.isEmpty(billing.getEmail()))
			emailAddresses.add(billing.getEmail());
		if (!Strings.isEmpty(contractor.getPrimaryContact().getEmail())) {
			if (!emailAddresses.contains(contractor.getPrimaryContact().getEmail()))
				emailAddresses.add(contractor.getPrimaryContact().getEmail());
		}

		emailBuilder.setToAddresses(emailAddresses.get(0));

		if (emailAddresses.size() > 1)
			emailBuilder.setCcAddresses(emailAddresses.get(1));
		
		EmailQueue email = emailBuilder.build();
		if (invoice.getStatus().isPaid())
			email.setSubject("PICS Payment Receipt for Invoice " + invoice.getId());
		email.setPriority(60);
		email.setHtml(true);
		email.setViewableById(Account.PicsID);
		EmailSender.send(email);

		return email;
	}
}
