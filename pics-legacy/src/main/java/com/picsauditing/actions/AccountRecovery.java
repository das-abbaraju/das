package com.picsauditing.actions;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailTemplate;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import com.picsauditing.validator.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class AccountRecovery extends PicsActionSupport {
	@Autowired
	private EmailTemplateDAO emailTemplateDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private EmailSender emailSender;
	@Autowired
	private InputValidator inputValidator;

	private String email, username;
	private EmailBuilder emailBuilder;
	private URLUtils urlUtils;

	@Anonymous
	@Override
	public String execute() throws Exception {
		return "password";
	}

	@Anonymous
	public String recoverUsername() {
		return "username";
	}

	@Anonymous
	public String findName() throws Exception {
		String accountRecoveryPage = urlUtils().getActionUrl("AccountRecovery", "recoverUsername");

		String emailError = inputValidator.validateEmail(email);
		if (!emailError.equals(InputValidator.NO_ERROR)) {
			addActionError(getText(emailError));
			return "username";
		}

		email = email.trim();
		// if username starts with DELETED, remove from the matchingUsers list.
		List<User> matchingUsers = new Grepper<User>() {
			public boolean check(User user) {
				return user != null && !user.isDeleted();
			}
		}.grep(userDAO.findByEmail(email));

		if (matchingUsers.size() == 0) {
			addActionError(getText("AccountRecovery.error.EmailNotFound"));
			return setUrlForRedirect(accountRecoveryPage);
		}

		try {
			User user = matchingUsers.get(0);

			Map<String, Object> templateParameters = new TreeMap<>();
			templateParameters.put("user", user);
			templateParameters.put("users", matchingUsers);
			templateParameters.put("username", user.getName());

			EmailTemplate usernameReminder = emailTemplateDAO.find(EmailTemplate.USERNAME_REMINDER);
			EmailQueue emailQueue = email(user, usernameReminder, templateParameters);
			emailQueue.setCriticalPriority();

			emailSender.send(emailQueue);

			setActionMessageHeader(getText("AccountRecovery.EmailSentHeader"));
			addActionMessage(getTextParameterized("AccountRecovery.EmailSent2", email));
		} catch (Exception e) {
			addActionError(getText("AccountRecovery.error.NoEmailSent"));
		}

		if (hasActionErrors()) {
			return "username";
		} else {
			// If everything is ok
			String loginPage = urlUtils.getActionUrl("Login");
			return setUrlForRedirect(loginPage);
		}
	}

	@Anonymous
	public String resetPassword() {
		if (Strings.isEmpty(username) || username.startsWith("DELETE-")) {
			addActionError(getText("AccountRecovery.error.NoUserName"));
			return "password";
		}

		try {
			User user = userDAO.findName(username);

			if (user == null) {
				throw new Exception(getText("AccountRecovery.error.UserNotFound"));
			}
			// LW if the user has an inactive status, then not allow them to
			// recover the password.
			if (!user.isActiveB()) {
				addActionError(getText("AccountRecovery.error.UserNotActive"));
				throw new Exception(getText("AccountRecovery.error.UserNotActive"));
			}
			// Seeding the time in the reset hash so that each one will be
			// guaranteed unique
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			userDAO.save(user);

			String recoveryEmailStatus = sendRecoveryEmail(user);

			setActionMessageHeader(getText("AccountRecovery.EmailSentHeader"));
			addActionMessage(recoveryEmailStatus);


			return setUrlForRedirect("Login.action");

		} catch (Exception e) {
			addActionError(getText("AccountRecovery.error.UserNameNotFound"));
		}

		return "password";
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private EmailBuilder emailBuilder() {
		if (emailBuilder == null) {
			emailBuilder = new EmailBuilder();
		}

		return emailBuilder;
	}

	private URLUtils urlUtils() {
		if (urlUtils == null) {
			urlUtils = new URLUtils();
		}

		return urlUtils;
	}

	private String sendRecoveryEmail(User user) {
		String recoveryEmailStatus = "";

		try {
			Map<String, Object> templateParameters = new TreeMap<>();
			templateParameters.put("user", user);
			templateParameters.put("confirmLink", confirmLinkFor(user));

			EmailTemplate passwordReset = emailTemplateDAO.find(EmailTemplate.PASSWORD_RESET);
			EmailQueue emailQueue = email(user, passwordReset, templateParameters);
			emailQueue.setCriticalPriority();

			emailSender.send(emailQueue);

			recoveryEmailStatus = getTextParameterized("AccountRecovery.EmailSent", user.getEmail());
		} catch (Exception e) {
			recoveryEmailStatus = getText("AccountRecovery.error.ResetEmailError");
		}

		return recoveryEmailStatus;
	}

	private String confirmLinkFor(User user) {
		String serverName = getRequestHost().replace("http://", "https://");

		Map<String, Object> parameters = new TreeMap<>();
		parameters.put("username", user.getUsername());
		parameters.put("key", user.getResetHash());
		parameters.put("button", "reset");

		return serverName + urlUtils().getActionUrl("Login", parameters);
	}

	private EmailQueue email(User user, EmailTemplate template, Map<String, Object> templateParameters) throws
			Exception {
		emailBuilder().setTemplate(template);
		emailBuilder().setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
		emailBuilder().setToAddresses(user.getEmail());
		emailBuilder().addAllTokens(templateParameters);

		return emailBuilder().build();
	}
}