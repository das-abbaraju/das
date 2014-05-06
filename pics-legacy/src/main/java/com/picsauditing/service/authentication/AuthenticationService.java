package com.picsauditing.service.authentication;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.model.LoginContext;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.SessionCookie;
import com.picsauditing.security.SessionSecurity;
import com.picsauditing.service.user.UserService;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;
import javax.security.auth.login.FailedLoginException;

public class AuthenticationService {

	@Autowired
	private AppUserService appUserService;
	@Autowired
	private ProfileEntityService profileEntityService;
	@Autowired
	private UserService userService;

	@Transactional(propagation = Propagation.NESTED)
	public AppUser createNewAppUser(final String username, final String password) {
		validateUsernameAndPassword(username, password);

		return appUserService.generateNewAppUser(username, password);
	}

	private void validateUsernameAndPassword(String username, String password) {
		if (Strings.isEmpty(username) || Strings.isEmpty(password)) {
			throw new IllegalArgumentException("username and password cannot be blank");
		}

		if (!appUserService.isUserNameAvailable(username)) {
			throw new UsernameNotAvailableException("username " + username + " is not available");
		}
	}

	public String authenticateEmployeeGUARDUser(final String username, final String password, final boolean rememberMe) throws FailedLoginException {
		AppUser appUser = appUserService.findByUsernameAndUnencodedPassword(username, password);
		if (appUser == null) {
			throw new FailedLoginException("Could not authenticate username = " + username);
		}

		int appUserId = appUser.getId();

		Profile profile = profileEntityService.findByAppUserId(appUserId);
		if (profile == null) {
			throw new FailedLoginException("Profile was not found for appUserId = " + appUserId);
		}

		User user = userService.findByAppUserId(appUserId);

		return sessionCookieContent(appUserId, userId(user), profile.getId(), rememberMe);
	}

	private String sessionCookieContent(final int appUserId, final int picsUserId,
										final int profileId, final boolean rememberMe) {
		SessionCookie sessionCookie = createSessionCookie(rememberMe, profileId, appUserId, picsUserId);

		SessionSecurity.addValidationHashToSessionCookie(sessionCookie);

		return sessionCookie.toString();
	}

	private SessionCookie createSessionCookie(final boolean rememberMe, final int profileId, final int appUserID,
											  final int picsUserID) {
		SessionCookie sessionCookie = new SessionCookie();

		sessionCookie.setUserID(picsUserID);
		sessionCookie.setAppUserID(appUserID);
		sessionCookie.setProfileID(profileId);
		sessionCookie.setCookieCreationTime(DateBean.today());
		sessionCookie.putData(SessionCookie.REMEMBER_ME_DATA_KEY, rememberMe);

		return sessionCookie;
	}

	public LoginContext doPreLoginVerificationEG(final String username, final String password) throws AccountNotFoundException {
		AppUser appUser = appUserService.findByUsername(username);

		int appUserId = appUser.getId();
		User user = userService.findByAppUserId(appUserId);
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		if (profile == null) {
			throw new AccountNotFoundException("No user with username: " + username + " found.");
		}

		LoginContext response = new LoginContext();
		try {
			String cookieContent = sessionCookieContent(appUserId, userId(user), profile.getId(), true);

			response.setCookie(cookieContent);
			response.setAppUser(appUser);
			response.setProfile(profile);
			response.setUser(user);

			return response;
		} catch (Exception e) {
			// todo: make SURE its not a failed login, etc.
			throw new AccountNotFoundException("No user with username: " + username + " found.");
		}
	}

	private int userId(final User user) {
		return user == null ? 0 : user.getId();
	}
}
