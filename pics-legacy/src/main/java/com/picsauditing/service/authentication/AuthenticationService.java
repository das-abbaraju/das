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

	public String authenticateEmployeeGUARDUser(final String username, final String password, final boolean rememberMe)
			throws FailedLoginException {

		AppUser appUser = loadAppUser(username, password);

		int appUserId = appUser.getId();
		Profile profile = loadProfile(appUserId);
		User user = userService.findByAppUserId(appUserId);

		return getSessionCookieContent(appUserId, userId(user), profile.getId(), rememberMe);
	}

	public LoginContext doPreLoginVerificationEG(final String username, final String password)
			throws AccountNotFoundException, FailedLoginException {

		AppUser appUser = loadAppUser(username, password);

		int appUserId = appUser.getId();
		User user = userService.findByAppUserId(appUserId);
		Profile profile = loadProfile(appUserId);

		return buildLoginContext(username, appUser, appUserId, user, profile);
	}

	private AppUser loadAppUser(String username, String password) throws FailedLoginException {
		AppUser appUser = appUserService.findByUsernameAndUnencodedPassword(username, password);
		if (appUser == null) {
			throw new FailedLoginException("Could not authenticate username = " + username);
		}

		return appUser;
	}

	private Profile loadProfile(int appUserId) throws FailedLoginException {
		Profile profile = profileEntityService.findByAppUserId(appUserId);
		if (profile == null) {
			throw new FailedLoginException("Profile was not found for appUserId = " + appUserId);
		}

		return profile;
	}

    public LoginContext buildLoginContext(AppUser appUser) throws Exception {
        return buildLoginContext(appUser.getUsername(), appUser, appUser.getId(), userService.findByAppUserId(appUser.getId()), profileEntityService.findByAppUserId(appUser.getId()));
    }

	private LoginContext buildLoginContext(final String username, final AppUser appUser, final int appUserId,
										   final User user, final Profile profile)
			throws AccountNotFoundException {

		LoginContext response = new LoginContext();
		try {
			String cookieContent = getSessionCookieContent(appUserId, userId(user), profile.getId(), true);

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

	private String getSessionCookieContent(final int appUserId, final int picsUserId,
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

	private int userId(final User user) {
		return user == null ? 0 : user.getId();
	}
}
