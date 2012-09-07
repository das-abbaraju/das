package com.picsauditing.actions;

import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AccountRecovery extends PicsActionSupport {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private EmailSender emailSender;

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
		if (email.length()>0)
			email = email.trim();

		if (email == null || email.equals("")) {
			addActionError(getText("AccountRecovery.error.NoEmail"));
			return SUCCESS;
		}
		
		if (!EmailAddressUtils.isValidEmail(email)) {
			addActionError(getText("AccountRecovery.error.InvalidEmail"));
			return SUCCESS;
		}

		EmailBuilder emailBuilder = new EmailBuilder();

		List<User> matchingUsers = userDAO.findByEmail(email);
		//if username starts with DELETED, remove from the matchingUsers list.
		for (int count=0; count< matchingUsers.size(); count++){
			if (matchingUsers.get(count).getUsername().startsWith("DELETE-"))
				matchingUsers.remove(count);			
		}

		if (matchingUsers.size() == 0) {
			addActionError(getText("AccountRecovery.error.EmailNotFound"));
			return SUCCESS;
		}

		if (recaptcha == null)
			recaptcha = new Recaptcha();
		
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
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			emailBuilder.addToken("users", matchingUsers);
			emailBuilder.setToAddresses(email);
			emailBuilder.addToken("username", matchingUsers.get(0).getName());
			emailBuilder.addToken("user", matchingUsers.get(0));
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setCriticalPriority();

			emailSender.send(emailQueue);

			addActionMessage(getTextParameterized("AccountRecovery.EmailSent2", email));
		} catch (Exception e) {
			addActionError(getText("AccountRecovery.error.NoEmailSent"));
		}
		return SUCCESS;
	}
	
	@Anonymous
	public String resetPassword() {
		if (username == null || username.equals("")||username.startsWith("DELETE-")) {
			addActionError(getText("AccountRecovery.error.NoUserName"));
			return SUCCESS;
		}

		if (recaptcha == null)
			recaptcha = new Recaptcha();
		
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
			//LW if the user has an inactive status, then not allow them to recover the password.
			if (!user.isActiveB()){
				addActionError(getText("AccountRecovery.error.UserNotActive"));
				throw new Exception(getText("AccountRecovery.error.UserNotActive"));				
			}
			if (user == null)				
				throw new Exception(getText("AccountRecovery.error.UserNotFound"));
			
			// Seeding the time in the reset hash so that each one will be
			// guaranteed unique
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			userDAO.save(user);

			addAlertMessage(sendRecoveryEmail(user));

			return setUrlForRedirect("Login.action");

		} catch (Exception e) {
			addActionError(getText("AccountRecovery.error.UserNameNotFound"));
		}
		return SUCCESS;
	}

	public String sendRecoveryEmail(User user) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(85);
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			emailBuilder.addToken("user", user);

			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			String confirmLink = "http://www.picsorganizer.com/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setCriticalPriority();

			emailSender.send(emailQueue);
			return getTextParameterized("AccountRecovery.EmailSent", user.getEmail());
		} catch (Exception e) {
			return getText("AccountRecovery.error.ResetEmailError");
		}
	}

	public String sendActivationEmail(User user, Permissions permission) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(5);
			emailBuilder.setFromAddress(EmailAddressUtils.PICS_CUSTOMER_SERVICE_EMAIL_ADDRESS);
			emailBuilder.setBccAddresses(EmailAddressUtils.PICS_MARKETING_EMAIL_ADDRESS_WITH_NAME);
			emailBuilder.addToken("user", user);
			user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
			String confirmLink = "http://www.picsorganizer.com/Login.action?username="
					+ URLEncoder.encode(user.getUsername(), "UTF-8") + "&key=" + user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());
			emailBuilder.setPermissions(permission);

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setCriticalPriority();

			emailSender.send(emailQueue);
			return getTextParameterized("AccountRecovery.EmailSent", user.getEmail());
		} catch (Exception e) {
			return getText("AccountRecovery.error.ResetEmailError");
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