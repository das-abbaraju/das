package com.picsauditing.actions;

import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AccountRecovery extends PicsActionSupport {
	private String email, username;
	private User user;
	private UserDAO userDAO;

	public AccountRecovery(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public String execute() throws Exception {
		if (button == null)
			return SUCCESS;

		if ("Find Username".equals(button)) {
			if (email == null || email.equals("")) {
				addActionError("Please input an email address");
				return SUCCESS;
			}

			if (!Utilities.isValidEmail(email)) {
				addActionError("Please enter a valid email address.");
				return SUCCESS;
			}
			EmailBuilder emailBuilder = new EmailBuilder();

			List<User> matchingUsers = userDAO.findByEmail(email);
			if (matchingUsers.size() == 0) {
				addActionError("No account in our records has that email address.  Please verify it is "
						+ "the one you used when creating your PICS company profile.");
				return SUCCESS;
			}
			try {
				emailBuilder.setTemplate(86); // Password Reminder
				emailBuilder.setFromAddress("info@picsauditing.com");
				emailBuilder.addToken("users", matchingUsers);
				emailBuilder.setToAddresses(email);
				emailBuilder.addToken("username", matchingUsers.get(0).getName());
				emailBuilder.addToken("user", matchingUsers.get(0));
				EmailQueue emailQueue = emailBuilder.build();
				emailQueue.setPriority(100);

				EmailSender.send(emailQueue);

				addActionMessage("An email has been sent to this address: <b>" + email + "</b> "
						+ "with your PICS account username" + (matchingUsers.size() > 1 ? "s" : ""));
			} catch (Exception e) {
				addActionError("Failed to send emails");
			}
			return SUCCESS;
		} else if ("Reset Password".equals(button)) {
			if (username == null || username.equals("")) {
				addActionError("Please input a username");
				return SUCCESS;
			}
			try {
				user = userDAO.findName(username);
				if (user == null)
					throw new Exception("No such user exists");

				// Seeding the time in the reset hash so that each one will be
				// guaranteed unique
				user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
				userDAO.save(user);

				addActionMessage(sendRecoveryEmail(user));
			} catch (Exception e) {
				addActionError("No such user exists");
			}
			return SUCCESS;
		}

		return SUCCESS;
	}

	static public String sendRecoveryEmail(User user) {
		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(85);
			emailBuilder.setFromAddress("info@picsauditing.com");
			emailBuilder.addToken("user", user);

			String confirmLink = "http://www.picsauditing.com/Login.action?username=" + user.getUsername() + "&key="
					+ user.getResetHash() + "&button=reset";
			emailBuilder.addToken("confirmLink", confirmLink);
			emailBuilder.setToAddresses(user.getEmail());

			EmailQueue emailQueue;
			emailQueue = emailBuilder.build();
			emailQueue.setPriority(100);

			EmailSender sender = new EmailSender();
			sender.sendNow(emailQueue);
			return "An email has been sent to " + user.getEmail()
					+ ". This email includes a link to set or reset the password on the account.";
		} catch (Exception e) {
			return "An error occurred in sending the password reset email.";
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
}