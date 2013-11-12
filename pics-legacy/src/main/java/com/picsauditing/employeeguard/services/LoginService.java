package com.picsauditing.employeeguard.services;

import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.security.EncodedMessage;
import com.picsauditing.util.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.UriBuilder;

public class LoginService {
	@Autowired
	private AppUserDAO appUserDAO;

	private static final String key = "1eyndgv4iddubsry9u9kheniab7r4cvb";

	public JSONObject loginViaRest(String username, String password) {
		JSONObject json = new JSONObject();
		AppUser appUser = appUserDAO.findByUserName(username);

		if (appUser == null) {
			json.put("status", "FAIL");
		} else {
			Client client = Client.create(new DefaultClientConfig());
			String uri = authenticateByCredentialsLink(username, EncodedMessage.hash(password + appUser.getHashSalt()));
			WebResource webResource = client.resource(UriBuilder.fromUri(uri).build());
			ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
			String jsonString = response.getEntity(String.class);
			json = (JSONObject) JSONValue.parse(jsonString);
		}
		return json;
	}

	private String authenticateByCredentialsLink(String username, String password) {
		return requestHost() + "/AuthService!authenticateByCredentials.action" +
				"?apiKey=" + key +
				"&username=" + username +
				"&password=" + password;
	}

	private String requestHost() {
		String requestURL = ServletActionContext.getRequest().getRequestURL().toString();
		String requestURI = ServletActionContext.getRequest().getRequestURI();
		String requestHost = requestURL.replace(requestURI, Strings.EMPTY_STRING);

		return requestHost;
	}
}
