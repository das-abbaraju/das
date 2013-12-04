package com.picsauditing.employeeguard.services.factory;

import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.services.LoginService;
import com.picsauditing.security.SessionCookie;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mockito.Mockito;

import java.util.Date;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class LoginServiceFactory {
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String FAIL = "fail";

	private static LoginService loginService = Mockito.mock(LoginService.class);

	public static LoginService getLoginService() {
		Mockito.reset(loginService);

		SessionCookie sessionCookie = new SessionCookie();
		sessionCookie.setAppUserID(Identifiable.SYSTEM);
		sessionCookie.setCookieCreationTime(new Date());

		JSONObject success = (JSONObject) JSONValue.parse("{\"status\":\"SUCCESS\",\"cookie\":\"" + sessionCookie.toString() + "\"}");
		JSONObject fail = (JSONObject) JSONValue.parse("{\"status\":\"FAILURE\"}");

		when(loginService.loginViaRest(USERNAME, PASSWORD)).thenReturn(success);
		when(loginService.loginViaRest(eq(USERNAME), eq(PASSWORD), anyString())).thenReturn(success);
		when(loginService.loginViaRest(FAIL, FAIL)).thenReturn(fail);
		when(loginService.loginViaRest(eq(FAIL), eq(FAIL), anyString())).thenReturn(fail);

		return loginService;
	}
}
