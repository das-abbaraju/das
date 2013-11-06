package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.security.EncodedMessage;
import com.picsauditing.security.SessionCookie;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.Mockito.when;

public class AppUserServiceFactory {
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String FAIL = "fail";
	public static final String VALID_USERNAME = "valid username";

	private static AppUserService appUserService = Mockito.mock(AppUserService.class);

	public static AppUserService getAppUserService() throws Exception {
		Mockito.reset(appUserService);

		SessionCookie sessionCookie = new SessionCookie();
		sessionCookie.setAppUserID(Identifiable.SYSTEM);
		sessionCookie.setCookieCreationTime(new Date());

		JSONObject success = (JSONObject) JSONValue.parse("{\"id\":1,\"status\":\"SUCCESS\",\"cookie\":\"" + sessionCookie.toString() + "\"}");
		JSONObject fail = (JSONObject) JSONValue.parse("{\"status\":\"FAILURE\"}");

		when(appUserService.createNewAppUser(USERNAME, EncodedMessage.hash(PASSWORD))).thenReturn(success);
		when(appUserService.createNewAppUser(FAIL, EncodedMessage.hash(FAIL))).thenReturn(fail);
		when(appUserService.isUserNameAvailable(VALID_USERNAME)).thenReturn(true);

		return appUserService;
	}
}
