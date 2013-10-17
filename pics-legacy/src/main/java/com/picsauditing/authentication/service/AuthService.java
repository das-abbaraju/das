package com.picsauditing.authentication.service;

import com.picsauditing.access.ApiRequired;
import com.picsauditing.access.LoginService;
import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.employeeguard.services.ProfileService;
import com.picsauditing.security.SessionCookie;
import com.picsauditing.security.SessionSecurity;
import org.apache.struts2.interceptor.ParameterAware;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class AuthService extends PicsApiSupport implements ParameterAware {

	//TODO - logging

	private String ssoToken;
	private String username;
	private String password;

	@Autowired
	private LoginService loginService;
	@Autowired
	private AppUserDAO appUserDAO;
	@Autowired
	private ProfileService profileService;

	@ApiRequired
	public String execute() {
		json.put("status", "SUCCESS");
//		json.put("method", "execute");
//		json.put("username", appUserDAO.find(32722).getUsername());
		return JSON;
	}

	@ApiRequired
	public String checkUserName() {
		json.put("status", (appUserDAO.findListByUserName(username).size() < 1) ? "Available" : "Taken");
		return JSON;
	}

	@ApiRequired
	public String createNewAppUser() {
		AppUser newAppUser = new AppUser();
		newAppUser.setUsername(username);
		newAppUser.setPassword(password);

		appUserDAO.save(newAppUser);

		newAppUser = appUserDAO.findByUserNameAndPassword(username, password);

		json.put("status", "SUCCESS");
		json.put("id", newAppUser.getId());
		return JSON;
	}

	@ApiRequired
	public String authenticateByCredentials() throws Exception {
		//TODO - handle incorrect login attempt

		json = new JSONObject();

		try {
			//user = loginService.loginNormally(username, password);
			verifyAppUserExists(username);
			verifyPasswordIsCorrect(username, password);

			//addClientSessionCookieToResponse();

			//TODO - verifyUserStatusForLogin(user);
			//TODO - verifyPasswordIsNotExpired(user);
			//TODO - track and update last login
			//TODO - log login attempt and result

			json.put("cookie", sessionCookieContent(true, 0));
			json.put("status", "SUCCESS");
		} catch (Exception e) {
			json.put("status", "FAIL");
		}

		return JSON;
	}

	@ApiRequired
	public String authenticateByToken() {
		json = new JSONObject();
		json.put("method", "Token");
		return JSON;
	}

//	private void updateUserForSuccessfulLogin() {
//		user.unlockLogin();
//		user.setLastLogin(new Date());
//		userDAO.save(user);
//	}

	private void verifyAppUserExists(String username) throws Exception {
		if (appUserDAO.findListByUserName(username).size() == 0) {
			throw new Exception();
		}
	}

	private void verifyPasswordIsCorrect(String username, String password) throws Exception {
		if (appUserDAO.findByUserNameAndPassword(username, password) == null) {
			throw new Exception();
		}

	}

	private String sessionCookieContent(boolean rememberMe, int switchToUser) {
		int appUserID = appUserDAO.findListByUserName(username).get(0).getId();
		int picsUserID = userDAO.findUserIDByAppUserID(appUserID);

		SessionCookie sessionCookie = new SessionCookie();
		Date now = new Date();
		sessionCookie.setUserID(picsUserID);
		sessionCookie.setAppUserID(appUserID);
		sessionCookie.setProfileID(profileService.findByAppUserId(appUserID).getId());
		sessionCookie.setCookieCreationTime(now);
//		if (switchToUser > 0 && switchToUser != permissions.getUserId()) {
//			sessionCookie.putData("switchTo", switchToUser);
//		}
		sessionCookie.putData("rememberMe", rememberMe);
		SessionSecurity.addValidationHashToSessionCookie(sessionCookie);
		return sessionCookie.toString();
	}

	public String getSsoToken() {
		return ssoToken;
	}

	public void setSsoToken(String ssoToken) {
		this.ssoToken = ssoToken;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}