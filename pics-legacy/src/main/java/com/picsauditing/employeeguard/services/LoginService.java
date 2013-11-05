package com.picsauditing.employeeguard.services;

import com.picsauditing.dao.AppPropertyDAO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.UriBuilder;

public class LoginService {
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	private static final String key = "1eyndgv4iddubsry9u9kheniab7r4cvb";

	public JSONObject loginViaRest(String username, String password) {
		Client client = Client.create(new DefaultClientConfig());
		String uri = authenticateByCredentialsLink(username, password);
		WebResource webResource = client.resource(UriBuilder.fromUri(uri).build());
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		String jsonString = response.getEntity(String.class);
		return (JSONObject) JSONValue.parse(jsonString);
	}

	private String authenticateByCredentialsLink(String username, String password) {
		return requestHost() + "/AuthService!authenticateByCredentials.action" +
				"?apiKey=" + key +
				"&username=" + username +
				"&password=" + password;
	}

	private String requestHost() {
		return appPropertyDAO.getProperty("AuthServiceHost");
	}
}
