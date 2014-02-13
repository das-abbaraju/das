package com.picsauditing.service.email;

import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.mail.EmailBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.Map;

public class EmailBuilderService {

	@Autowired
	EmailBuilder emailBuilder;

	public EmailQueue buildEmail(EmailTemplate emailTemplate, User fromUser, String toEmail, Map<String, Object> tokenMap) throws IOException, EmailBuildErrorException {
		emailBuilder.setTemplate(emailTemplate);
		emailBuilder.setFromAddress(fromUser);
		emailBuilder.setToAddresses(toEmail);
		addTokens(tokenMap);
		EmailQueue emailQueue = emailBuilder.build();

		return emailQueue;
	}

	private EmailBuilder addTokens(Map<String, Object> tokenMap) {
		for (String key : tokenMap.keySet()) {
			emailBuilder.addToken(key, tokenMap.get(key));
		}
		return emailBuilder;
	}
}
