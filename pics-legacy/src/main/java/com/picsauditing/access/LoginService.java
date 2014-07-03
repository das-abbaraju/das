package com.picsauditing.access;

import com.picsauditing.access.model.LoginContext;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.service.user.UserService;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.util.Calendar;

@SuppressWarnings("serial")
public class LoginService {

    protected static final int MAX_FAILED_ATTEMPTS = 6;
    public static final String PICS_CORP = "@PICS.CORP";

    @Autowired
	protected UserService userService;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private ProfileEntityService profileService;
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ActiveDirectoryLdapAuthenticationProvider ldapActiveDirectoryAuthProvider;

    // todo: This is only part of the login process. Extract code from LoginController to make this complete.
	public User loginNormally(String username, String password) throws LoginException {

		User user = loadUserByUsername(username);
		return doPreLoginVerification(user, username, password).getUser();
	}

	public User getUserForUserName(String username) throws LoginException {
        AppUser appUser = appUserService.findByUsername(username);
        User user;
        if (appUser == null) {
            throw new AccountNotFoundException("No user with username: " + username + " found.");
        } else {
            user = userDAO.findUserByAppUserID(appUser.getId());
        }

		return user;
	}

	public LoginContext doPreLoginVerification(User user, String username, String password) throws LoginException {
        boolean result = adLDAPLoginAuthentication(username, password);
        if(!result) {
            verifyUserExists(user, username);
            verifyUserStatusForLogin(user);
            verifyPasswordIsCorrect(user, password);
            verifyPasswordIsNotExpired(user);
        }

        LoginContext loginContext = new LoginContext();
        loginContext.setUser(user);

		return loginContext;
	}

    private boolean adLDAPLoginAuthentication(String username, String password) throws FailedLoginException{
        ldapActiveDirectoryAuthProvider.setConvertSubErrorCodesToExceptions(true);
        String ldapUser = username + PICS_CORP;
        try {
            Authentication result = ldapActiveDirectoryAuthProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(ldapUser, password));
            return result.isAuthenticated();
        }
        catch(AuthenticationException ace){
            throw new FailedLoginException("Bad Credentials for user: " + username);
        }
    }


	// todo: This is only part of the login process. Extract code from LoginController to make this complete.
	public LoginContext loginForResetPassword(String username, String key) throws LoginException {
        AppUser appUser = appUserService.findByUsername(username);
        User user = userService.findByAppUserId(appUser.getId());
        Profile profile = profileService.findByAppUserId(appUser.getId());

        if (appUser == null) {
            throw new LoginException("Could not find account");
        }

        if (profile == null && user == null) {
            throw new AccountNotFoundException("No PicsOrg or EmployeeGUARD account found.");
        }

		processReset(key, appUser);
        LoginContext loginContext = new LoginContext();
        loginContext.setAppUser(appUser);

        if (profile != null) {
            loginContext.setProfile(profile);
        }

        if (user != null) {
            verifyUserExists(user, username);
            verifyUserStatusForLogin(user);
            verifyPasswordIsNotExpired(user);
            loginContext.setUser(user);
        }

		return loginContext;
	}

	public HomePageType postLoginHomePageTypeForRedirect(String preLoginUrl, User user) {
		if (user != null) {
			Account account = user.getAccount();
			if (account.isContractor()) {
				if (account.getStatus().isDeactivated()) {
					return HomePageType.Deactivated;
				} else if (account.getStatus().isDeclined()) {
					return HomePageType.Declined;
				} else {
					if (account.getStatus().isPending() && Strings.isNotEmpty(preLoginUrl) && preLoginUrl.contains("InvoiceDetail")) {
						for (Invoice invoice : ((ContractorAccount) account).getInvoices()) {
							String linkToInvoice = "InvoiceDetail.action?invoice.id=" + invoice.getId();
							if (preLoginUrl.contains(linkToInvoice)) {
								return HomePageType.PreLogin;
							}
						}
					}

					ContractorRegistrationStep step = ContractorRegistrationStep.getStep((ContractorAccount) account);
					if (step.isDone() && Strings.isNotEmpty(preLoginUrl)) {
						return HomePageType.PreLogin;
					} else {
						return HomePageType.ContractorRegistrationStep;
					}
				}
			} else {
				if (Strings.isNotEmpty(preLoginUrl)) {
					return HomePageType.PreLogin;
				} else {
					return HomePageType.HomePage;
				}
			}
		} else {
			return HomePageType.EmployeeGUARD;
		}
	}

	private void processReset(String key, AppUser appUser) throws InvalidResetKeyException {
		verifyResetHashIsValid(appUser, key);
		prepareUserForLoginAfterReset(appUser);
	}

	private void verifyUserExists(User user, String username) throws AccountNotFoundException {
		if (user == null) {
			throw new AccountNotFoundException("No user with username: " + username + " found.");
		}
	}

	protected User loadUserByUsername(String username) {
		return userService.loadUserByUsername(username);
	}

	private boolean verifyUserStatusForLogin(User user) throws AccountLockedException, AccountInactiveException {
		if (user.isLocked()) {
			throw new AccountLockedException("Account is locked for user: " + user.getUsername());

		} else if (!isUserActive(user)) {
			throw new AccountInactiveException("Account is inactive for user: " + user.getUsername(), user.getUsername());
		}
		return true;
	}

	protected boolean isUserActive(User user) {
		return userService.isUserActive(user);
	}

	private boolean verifyPasswordIsCorrect(User user, String password) throws FailedLoginException, FailedLoginAndLockedException {
		if (!user.isEncryptedPasswordEqual(password)) {
			updateUserForFailedPassword(user);
			if (user.getFailedAttempts() > MAX_FAILED_ATTEMPTS) {
				throw new FailedLoginAndLockedException("Maximum password attempts exceeded for user: " + user.getUsername() + ". Account locked.", user.getUsername());
			} else {
				throw new FailedLoginException("Incorrect password for user: " + user.getUsername());
			}

		}
		return true;
	}

	private void updateUserForFailedPassword(User user) {
		user.setFailedAttempts(user.getFailedAttempts() + 1);
		if (user.getFailedAttempts() > MAX_FAILED_ATTEMPTS) {
			lockoutUserForAnHour(user);
		}
	}

	private void lockoutUserForAnHour(User user) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, 1);
		user.setFailedAttempts(0);
		user.setLockUntil(calendar.getTime());
	}

	private boolean verifyPasswordIsNotExpired(User user) throws PasswordExpiredException {
		if (isUserPasswordExpired(user)) {
			throw new PasswordExpiredException("Password expired for user: " + user.getUsername(), user.getUsername());
		}
		return true;
	}

	protected boolean isUserPasswordExpired(User user) {
		return userService.isPasswordExpired(user);
	}

	private boolean verifyResetHashIsValid(AppUser appUser, String key) throws InvalidResetKeyException {
		if (Strings.isNotEmpty(key) && appUser != null) {
			if (appUser.getResetHash() == null || !appUser.getResetHash().equals(key)) {
				throw new InvalidResetKeyException("Reset key doesn't match for user: " + appUser.getUsername(), appUser.getUsername());
			}
		}
		return true;
	}

	private void prepareUserForLoginAfterReset(AppUser appUser) {
        if (appUser != null) {
            appUser.setResetHash(null);
        }

        User user = userService.findByAppUserId(appUser.getId());

        if (user != null) {
            user.setForcePasswordReset(true);
			user.unlockLogin();
			user.setPasswordChanged(null);
		}
	}

}
