package com.picsauditing.actions;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Anonymous;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AccountRecovery extends PicsActionSupport {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private EmailSenderSpring emailSender;

	private String email, username;
	private User user;
	private Recaptcha recaptcha;

	@Anonymous
	@Override
	public String execute() throws Exception {
		recaptcha = new Recaptcha();

		return SUCCESS;
	}
	
	@Anonymous
	public String findName() {
		if (email == null || email.equals("")) {
			addActionError(getText("AccountRecovery.error.NoEmail"));
			return SUCCESS;
		}

		if (!Strings.isValidEmail(email)) {
			addActionError(getText("AccountRecovery.error.InvalidEmail"));
			return SUCCESS;
		}

		EmailBuilder emailBuilder = new EmailBuilder();

		List<User> matchingUsers = userDAO.findByEmail(email);

		if (matchingUsers.size() == 0) {
			addActionError(getText("AccountRecovery.error.EmailNotFound"));
			return SUCCESS;
		}

		Boolean response = recaptcha.isRecaptchaResponseValid();
		if (response == null) {
			addActionError(getText("AccountRecovery.error.ReCaptchaCommProblem"));
			return SUCCESS;
		}

		if (!response) {
			addActionError(getText("AccountRecovery.error.ReCaptchaMismatch"));
			return SUCCESS;
		}

		try {
			emailBuilder.setTemplate(86); // Username Reminder
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			emailBuilder.addToken("users", matchingUsers);
			emailBuilder.setToAddresses(email);
			emailBuilder.addToken("username", matchingUsers.get(0).getName());
			emailBuilder.addToken("user", matchingUsers.get(0));
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(100);

			emailSender.send(emailQueue);

			addActionMessage(getTextParameterized("AccountRecovery.EmailSent2", email));
		} catch (Exception e) {
			addActionError(getText("AccountRecovery.error.NoEmailSent"));
		}
		return SUCCESS;
	}
	
	@Anonymous
	public String resetPassword() {
		if (username == null || username.equals("")) {
			addActionError(getText("AccountRecovery.error.NoUserName"));
			return SUCCESS;
		}

		Boolean response = recaptcha.isRecaptchaResponseValid();
		if (response == null) {
			addActionError(getText("AccountRecovery.error.ReCaptchaCommProblem"));
			return SUCCESS;
		}

		if (!response) {
			addActionError(getText("AccountRecovery.error.ReCaptchaMismatch"));
			return SUCCESS;
		}

		try {
			user = userDAO.findName(username);
			if (user == null)
				throw new Exception(getText("AccountRecovery.error.UserNotFound"));

			// Seeding the time in the reset hash so that each one will be
			// guaranteed unique
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			userDAO.save(user);

			addActionMessage(sendRecoveryEmail(user));
		} catch (Exception e) {
			addActionError(getText("AccountRecovery.error.UserNameNotFound"));
		}
		return SUCCESS;
	}

	static public String sendRecoveryEmail(User user) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(85);
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			emailBuilder.addToken("user", user);

			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			String confirmLink = "http://www.picsorganizer.com/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setPriority(100);

			EmailSenderSpring emailSenderStatic = (EmailSenderSpring) SpringUtils.getBean("EmailSenderSpring");
			emailSenderStatic.send(emailQueue);
			return I18nCache.getInstance().getText("AccountRecovery.EmailSent", null, user.getEmail());
		} catch (Exception e) {
			return I18nCache.getInstance().getText("AccountRecovery.error.ResetEmailError", null);
		}
	}

	static public String sendActivationEmail(User user, Permissions permission) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(5);
			emailBuilder.setFromAddress("\"PICS Customer Service\"<info@picsauditing.com>");
			emailBuilder.setBccAddresses("\"PICS Marketing\"<marketing@picsauditing.com>");
			emailBuilder.addToken("user", user);
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			String confirmLink = "http://www.picsorganizer.com/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());
			emailBuilder.setPermissions(permission);

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setPriority(100);

			EmailSenderSpring emailSenderStatic = (EmailSenderSpring) SpringUtils.getBean("EmailSenderSpring");
			emailSenderStatic.send(emailQueue);
			return I18nCache.getInstance().getText("AccountRecovery.EmailSent", null, user.getEmail());
		} catch (Exception e) {
			return I18nCache.getInstance().getText("AccountRecovery.error.ResetEmailError", null);
		}
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

	public Recaptcha getRecaptcha() {
		return recaptcha;
	}

	public void setRecaptcha(Recaptcha recaptcha) {
		this.recaptcha = recaptcha;
	}
}