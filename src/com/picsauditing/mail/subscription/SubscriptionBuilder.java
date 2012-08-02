package com.picsauditing.mail.subscription;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

public abstract class SubscriptionBuilder {
	@Autowired
	private EmailSender sender;
	@Autowired
	private EmailSubscriptionDAO subscriptionDAO;

	public void sendSubscription(EmailSubscription subscription) throws IOException, MessagingException {
		Map<String, Object> tokens = process(subscription);
		EmailQueue queue = buildEmail(subscription, tokens);

		if (queue != null)
			sender.sendNow(queue);

		subscription.setLastSent(new Date());
		subscriptionDAO.save(subscription);
		tokens.clear();
	}

	protected abstract Map<String, Object> process(EmailSubscription subscription) throws IOException;

	private EmailQueue buildEmail(EmailSubscription subscription, Map<String, Object> tokens) throws IOException {
		if (tokens.size() > 0) {
			int templateID = subscription.getSubscription().getTemplateID();
			User user = subscription.getUser();

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(templateID);
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			// TODO remove this after we update the templates from username to
			// user.name
			emailBuilder.addToken("username", user.getName());
			emailBuilder.addToken("user", user);

			String seed = "u" + user.getId() + "t" + templateID;

			// TODO: change this in the templates String
			String confirmLink = "http://www.picsorganizer.com/EmailUserUnsubscribe.action?id=" + user.getId()
					+ "&sub=" + subscription.getSubscription() + "&key=" + Strings.hashUrlSafe(seed);

			emailBuilder.addToken("confirmLink", confirmLink);

			emailBuilder.addToken("subscription", subscription);

			emailBuilder.addAllTokens(tokens);
			emailBuilder.setToAddresses(user.getEmail());

			try {
				// If contractor subscription send replies to CSR
				if (user.getAccount().isContractor()) {
					ContractorAccount c = (ContractorAccount) user.getAccount();
					emailBuilder.addToken("contractor", c);
					emailBuilder.setConID(c.getId());
					if (c.getAuditor() != null)
						emailBuilder.setFromAddress("\"" + c.getAuditor().getName() + "\"<" + c.getAuditor().getEmail()
								+ ">");
					// If operator subscription send replies to AM
				} else if (user.getAccount().isOperatorCorporate()) {
					OperatorAccount o = (OperatorAccount) user.getAccount();
					for (AccountUser au : o.getAccountUsers()) {
						if (au.getRole().equals(UserAccountRole.PICSAccountRep))
							emailBuilder.setFromAddress("\"" + au.getUser().getName() + "\"<" + au.getUser().getEmail()
									+ ">");
					}
				}
			} catch (Exception e) {

			}

			EmailQueue email = emailBuilder.build();
			// TODO: Make this solution more extensible
			email.setViewableById(subscription.getSubscription().getViewableBy());
			email.setCreatedBy(user);

			return email;
		}

		return null;
	}
}
