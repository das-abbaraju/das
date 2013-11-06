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
		String path = "/AuthService!authenticateByCredentials.action";
		String query = "?apiKey=" + key +
				"&username=" + username +
				"&password=" + password;

		return (JSONObject) JSONValue.parse(callRESTService(path, query));
	}

	private String callRESTService(String path, String query) {
		String host = requestHost();
		UriBuilder uriBuilder = UriBuilder.fromPath(path);
		uriBuilder.replaceQuery(query);
		uriBuilder.host(host);
		uriBuilder.scheme("localhost".equals(host) ? "http" : "https");
		uriBuilder.port(("localhost".equals(host) ? 8080 : -1));

		Client client = Client.create(new DefaultClientConfig());
		WebResource webResource = client.resource(uriBuilder.build());
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		return response.getEntity(String.class);
	}

	private String requestHost() {
		return appPropertyDAO.getProperty("AuthServiceHost");
	}
}
