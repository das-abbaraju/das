package com.picsauditing.access;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountLockedException;
import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import java.util.Calendar;

@SuppressWarnings("serial")
public class LoginService {

	@Autowired
	protected UserService userService;
	protected static final int MAX_FAILED_ATTEMPTS = 6;

	// todo: This is only part of the login process. Extract code from LoginController to make this complete.
	public User loginNormally(String username, String password) throws LoginException {

		User user = loadUserByUsername(username);
		return loginNormally(user, username, password);
	}

	public User loginNormally(User user, String username, String password) throws LoginException {
		verifyUserExists(user, username);
		verifyUserStatusForLogin(user);
		verifyPasswordIsCorrect(user, password);
		verifyPasswordIsNotExpired(user);

		return user;
	}

	// todo: This is only part of the login process. Extract code from LoginController to make this complete.
	public User loginForResetPassword(String username, String key) throws LoginException {

		User user = loadUserByUsername(username);

		processReset(key, user);

		verifyUserExists(user, username);
		verifyUserStatusForLogin(user);
		verifyPasswordIsNotExpired(user);

		return user;
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

	private void processReset(String key, User user) throws InvalidResetKeyException {
		verifyResetHashIsValid(user, key);
		prepareUserForLoginAfterReset(user);
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

	private boolean verifyResetHashIsValid(User user, String key) throws InvalidResetKeyException {
		if (Strings.isNotEmpty(key) && user != null) {
			if (user.getResetHash() == null || !user.getResetHash().equals(key)) {
				throw new InvalidResetKeyException("Reset key doesn't match for user: " + user.getUsername(), user.getUsername());
			}
		}
		return true;
	}

	private void prepareUserForLoginAfterReset(User user) {
		if (user != null) {
			user.setForcePasswordReset(true);
			user.setResetHash("");
			user.unlockLogin();
            user.setPasswordChanged(null);
		}
	}

}
