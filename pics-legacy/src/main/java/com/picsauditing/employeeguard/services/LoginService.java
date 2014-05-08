package com.picsauditing.employeeguard.services;

import com.picsauditing.access.model.LoginContext;
import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.employeeguard.entities.Profile;
import com.picsauditing.employeeguard.services.entity.ProfileEntityService;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.security.EncodedMessage;
import com.picsauditing.service.AppPropertyService;
import com.picsauditing.util.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.AccountNotFoundException;
import javax.ws.rs.core.UriBuilder;

public class LoginService {

    Logger LOG = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private AppUserDAO appUserDAO;
    @Autowired
	private AppPropertyService appPropertyService;

	private static final String key = "1eyndgv4iddubsry9u9kheniab7r4cvb";
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private ProfileEntityService profileEntityService;

    public JSONObject loginViaRest(String username, String password) {
		return loginViaRest(username, password, null);
	}

	public JSONObject loginViaRest(String username, String password, String hashCode) {
		JSONObject json = new JSONObject();
		AppUser appUser = appUserDAO.findByUserName(username);

		if (appUser == null) {
			json.put("status", "FAIL");
		} else {
            String query = buildQueryString(username, EncodedMessage.hash(password + appUser.getHashSalt()), hashCode);
			String jsonString = callRESTService("/AuthService!authenticateByCredentials.action", query);
			json = (JSONObject) JSONValue.parse(jsonString);
		}

		return json;
	}

    private String buildQueryString(String username, String password, String hashCode) {
        return "apiKey=" + key
                + "&username=" + username
                + "&password=" + password
                + (Strings.isNotEmpty(hashCode) ? "&hashCode=" + hashCode : Strings.EMPTY_STRING);
    }

    private String callRESTService(String path, String query) {
        String host = getRequestHost();
        UriBuilder uriBuilder = UriBuilder.fromPath(path);
        uriBuilder.replaceQuery(query);
        uriBuilder.host(host);
        uriBuilder.scheme("localhost".equals(host) ? "http" : "https");
        uriBuilder.port(("localhost".equals(host) ? getRequestHostPort() : -1));

		LOG.info(uriBuilder.toString());

        try {
            Client client = Client.create(new DefaultClientConfig());
            WebResource webResource = client.resource(uriBuilder.build());
            ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
            return response.getEntity(String.class);
        } catch (Exception e) {
            return handleRequestException(path, query, e);
        }
    }

    private String handleRequestException(String path, String query, Exception e) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Error in LoginService for request{}", path + "?" + query, e);
        }

	    JSONObject jsonObject = new JSONObject();
	    jsonObject.put("status", "FAIL");
	    return jsonObject.toString();
    }

	private String getRequestHost() {
		return appPropertyService.getPropertyString(AppProperty.AUTH_SERVICE_HOST);
	}

	private int getRequestHostPort() {
		return appPropertyService.getPropertyInt(AppProperty.AUTH_SERVICE_HOST_PORT, 8080);
	}

    public LoginContext doPreLoginVerificationEG(String username, String password) throws AccountException {
        AppUser appUser = appUserService.findAppUser(username);
        Profile profile = profileEntityService.findByAppUserId(appUser.getId());
        LoginContext response = new LoginContext();
        if (profile == null) {
            throw new AccountNotFoundException("No user with username: " + username + " found.");
        }
        else {
            JSONObject result = loginViaRest(username, password);

            response.setAppUser(appUser);
            response.setProfile(profile);

            if ("SUCCESS".equals(result.get("status").toString())) {
                response.setCookie(result.get("cookie").toString());
                return response;
            } else {
                throw new AccountNotFoundException("No user with username: " + username + " found.");   // todo: make SURE its not a failed login, etc.
            }
        }
    }

}
