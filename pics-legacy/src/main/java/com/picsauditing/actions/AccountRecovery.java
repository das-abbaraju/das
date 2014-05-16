package com.picsauditing.actions;

import com.picsauditing.PICS.Grepper;
import com.picsauditing.access.Anonymous;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
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
    private AppUserService appUserService;
    @Autowired
    private ProfileEntityService profileService;
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

        final AppUser appUser = appUserService.findByUsername(username);

        if (appUser == null) {
            messages.addUsernameNotFoundError();
        } else {
            final User user = userDAO.findName(username);
            final Profile profile = profileService.findByAppUserId(appUser.getId());

            if (user == null && profile == null) {
                messages.addNoValidAccountFound();
            } else if (!user.isActiveB() && profile == null) {
                messages.addUserNotActiveError();
            } else {
                try {
                    addResetHashTo(appUser);
                    appUserService.save(appUser);

                    if (user != null) {
                        emails.sendRecoveryEmail(user, getRequestHost());
                        messages.successfulEmailSendTo(user.getEmail());
                    } else {
                        emails.sendRecoveryEmail(profile, getRequestHost());
                        messages.successfulEmailSendTo(profile.getEmail());
                    }

                    return setUrlForRedirect("Login.action");

                } catch (EmailBuildErrorException e) {
                    messages.failedEmailSend();
                } catch (IOException e) {
                    messages.addUsernameNotFoundError();
                }
            }
        }
        return PASSWORD;
	}

    private void addResetHashTo(AppUser appUser) {
        appUser.setResetHash(Strings.hashUrlSafe("u" + appUser.getId() + String.valueOf(new Date().getTime())));
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