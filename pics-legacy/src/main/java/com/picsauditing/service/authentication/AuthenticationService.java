package com.picsauditing.service.authentication;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.access.model.LoginContext;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.security.EncodedMessage;
import com.picsauditing.security.SessionCookie;
import com.picsauditing.security.SessionSecurity;
import com.picsauditing.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;

public class AuthenticationService {

	// TODO: Replace with AppUserService
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private ProfileEntityService profileEntityService;
	// TODO: Replace with user service
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private AppUserService appUserService;

	@Transactional(propagation = Propagation.NESTED)
	public AppUser createNewAppUser(final String username, final String password) {
		validateUsernameAndPassword(username, password);

		return createAppUser(username, password);
	}

	private void validateUsernameAndPassword(String username, String password) {
		if (Strings.isEmpty(username) || Strings.isEmpty(password)) {
			throw new IllegalArgumentException("username and password cannot be blank");
		}

		if (isDuplicateUserName(username)) {
			throw new DuplicateUsernameException("username " + username + " is not available");
		}
	}

	private AppUser createAppUser(String username, String password) {
		AppUser newAppUser = new AppUser();

		newAppUser.setUsername(username);
		newAppUser = appUserDAO.save(newAppUser);

		String hashSalt = generateHashSalt(newAppUser);
		newAppUser.setHashSalt(hashSalt);
		newAppUser.setPassword(encodePassword(password, hashSalt));

		return appUserDAO.save(newAppUser);
	}

	private String generateHashSalt(AppUser newAppUser) {
		return Integer.toString(newAppUser.getId());
	}

	private String encodePassword(String password, String hashSalt) {
		return EncodedMessage.hash(password + hashSalt);
	}

	public boolean isDuplicateUserName(final String username) {
		return (appUserDAO.findListByUserName(username).size() >= 1);
	}

	public String authenticateEmployeeGUARDUser(final String username, final String password, final boolean rememberMe) throws Exception {
		AppUser appUser = appUserDAO.findByUserName(username);
		appUser = appUserDAO.findByUserNameAndPassword(username, encodePassword(password, appUser.getHashSalt()));

		//
		if (appUser == null) {
			throw new RuntimeException("Could not authenticate username = " + username);
		}

		int appUserId = appUser.getId();

		Profile profile = verifyEmployeeGuardStatus(appUserId);
		if (profile == null) {
			throw new RuntimeException("Profile was not found for appUserId = " + appUserId);
		}

		return sessionCookieContent(appUserId, findUserId(appUserId), profile.getId(), rememberMe);
	}

	private int findUserId(final int appUserId) {
		User user = userDAO.findUserByAppUserID(appUserId);
		if (user == null) {
			return 0;
		}

		return user.getId();
	}

	private AppUser findAppUser(final String username, final String password) {
		return appUserDAO.findByUserNameAndPassword(username, password);
	}

	private Profile verifyEmployeeGuardStatus(final int appUserId) throws Exception {
		return profileEntityService.findByAppUserId(appUserId);
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
		sessionCookie.putData("rememberMe", rememberMe);

		return sessionCookie;
	}

	public LoginContext doPreLoginVerificationEG(final String username, final String password) throws AccountNotFoundException {
		AppUser appUser = appUserService.findAppUser(username);
		Profile profile = profileEntityService.findByAppUserId(appUser.getId());

		if (profile == null) {
			throw new AccountNotFoundException("No user with username: " + username + " found.");
		}

		LoginContext response = new LoginContext();
		try {
			String cookieContent = authenticateEmployeeGUARDUser(username, password, true);
			response.setCookie(cookieContent);
			response.setAppUser(appUser);
			response.setProfile(profile);
			return response;
		} catch (Exception e) {
			// todo: make SURE its not a failed login, etc.
			throw new AccountNotFoundException("No user with username: " + username + " found.");
		}
	}
}
