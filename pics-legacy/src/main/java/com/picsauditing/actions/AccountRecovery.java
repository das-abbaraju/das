package com.picsauditing.actions;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuildErrorException;
import com.picsauditing.service.email.AccountRecoveryEmailService;
import com.picsauditing.util.Strings;
import com.picsauditing.util.URLUtils;
import com.picsauditing.validator.InputValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class AccountRecovery extends PicsActionSupport {
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private InputValidator inputValidator;
    @Autowired
    private AccountRecoveryEmailService emails;

	private String email, username;
	private URLUtils urlUtils;

    private AccountRecoveryMessages messages = new AccountRecoveryMessages(this);

    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";

    private static final Grepper<User> recoverableUserFilter = new Grepper<User>() {
        @Override
        public boolean check(User user) {
            return user != null && !user.isDeleted();
        }
    };

	@Anonymous
	@Override
	public String execute() throws Exception {
		return PASSWORD;
	}

	@Anonymous
	public String recoverUsername() {
		return USERNAME;
	}

	@Anonymous
	public String findName() throws Exception {

        if (!inputValidator.validateEmail(email).equals(InputValidator.NO_ERROR)) {
            messages.addNoUserNameError();
			return USERNAME;
		}

		final List<User> matchingUsers = recoverableUserFilter.grep(userDAO.findByEmail(email.trim()));

		if (matchingUsers.isEmpty()) {
            messages.addUserEmailNotFoundError();
            return accountRecoveryRedirect();
		} else {
            try {
                emails.sendUsernameRecoveryEmail(matchingUsers);
                messages.addUsernameEmailSentMessage();
                return loginRedirect();
            } catch (Exception e) {
                messages.addNoEmailSentError();
                return USERNAME;
            }
        }
	}



    private String loginRedirect() throws IOException {
        String loginPage = urlUtils().getActionUrl("Login");
        return setUrlForRedirect(loginPage);
    }

    private String accountRecoveryRedirect() throws IOException {
        String accountRecoveryPage = urlUtils().getActionUrl("AccountRecovery", "recoverUsername");
        return setUrlForRedirect(accountRecoveryPage);
    }

	@Anonymous
	public String resetPassword() {
		if (Strings.isEmpty(username) || username.startsWith("DELETE-")) {
            messages.addBlankUsernameSubmittedError();
			return PASSWORD;
		}

        final User user = userDAO.findName(username);

        if (user == null) {
            messages.addUserNotFoundError();
        } else if (!user.isActiveB()) {
            messages.addUserNotActiveError();
        } else {
            try {

                addResetHashTo(user);
                userDAO.save(user);

                emails.sendRecoveryEmail(user, getRequestHost());
                messages.successfulEmailSendTo(user);

                return setUrlForRedirect("Login.action");

            } catch (EmailBuildErrorException e) {
                messages.failedEmailSend();
            } catch (IOException e) {
                messages.addUsernameNotFoundError();
            }
        }

        return PASSWORD;

	}

    private void addResetHashTo(User user) {
        // Seeding the time in the reset hash so that each one will be
        // guaranteed unique
        user.setResetHash(Strings.hashUrlSafe("u" + user.getId() + String.valueOf(new Date().getTime())));
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


	private URLUtils urlUtils() {
		if (urlUtils == null) {
			urlUtils = new URLUtils();
		}
		return urlUtils;
	}


}