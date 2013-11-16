package com.picsauditing.employeeguard.services;

import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.security.EncodedMessage;
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

import javax.ws.rs.core.UriBuilder;

public class LoginService {

    Logger LOG = LoggerFactory.getLogger(LoginService.class);

	@Autowired
	private AppUserDAO appUserDAO;
    @Autowired
    private AppPropertyDAO appPropertyDAO;

	private static final String key = "1eyndgv4iddubsry9u9kheniab7r4cvb";

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
        String host = requestHost();
        UriBuilder uriBuilder = UriBuilder.fromPath(path);
        uriBuilder.replaceQuery(query);
        uriBuilder.host(host);
        uriBuilder.scheme("localhost".equals(host) ? "http" : "https");
        //uriBuilder.port(("localhost".equals(host) ? 8080 : -1));

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

    private String requestHost() {
        return appPropertyDAO.getProperty("AuthServiceHost");
    }
}
