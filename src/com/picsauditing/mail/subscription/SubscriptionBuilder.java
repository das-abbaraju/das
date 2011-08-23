package com.picsauditing.mail.subscription;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;

public abstract class SubscriptionBuilder {
	@Autowired
	protected EmailSenderSpring sender;

	Map<String, Object> tokens = new HashMap<String, Object>();

	public abstract void process(EmailSubscription subscription) throws IOException;

	protected EmailQueue buildEmail(EmailSubscription subscription) throws IOException {
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
			/*
			 * TODO: change this in the templates String
			 * 
			 * confirmLink = serverName + "EmailUserUnsubscribe.action?id=" + user.getId() + "&sub=" + subscription +
			 * "&key=" + Strings.hashUrlSafe(seed);
			 * 
			 * emailBuilder.addToken("confirmLink", confirmLink);
			 */

			emailBuilder.addToken("subscription", subscription);

			emailBuilder.addAllTokens(tokens);
			emailBuilder.setToAddresses(user.getEmail());

			EmailQueue email = emailBuilder.build();
			email.setViewableById(Account.PRIVATE);
			email.setCreatedBy(user);

			return email;
		}

		return null;
	}
}
