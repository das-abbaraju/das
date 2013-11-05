package com.picsauditing.authentication.service;

import com.picsauditing.dao.AppPropertyDAO;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.core.UriBuilder;

public class AppUserService {
	@Autowired
	private AppPropertyDAO appPropertyDAO;

	private static final String key = "1eyndgv4iddubsry9u9kheniab7r4cvb";

	public boolean isUserNameAvailable(String userName) {
		String uri = "/AuthService!checkUserName.action?apiKey=" + key + "&username=" + userName;

		Client client = Client.create(new DefaultClientConfig());
		WebResource webResource = client.resource(UriBuilder.fromUri(requestHost() + uri).build());
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		String output = response.getEntity(String.class);
		return output.contains("Available");
	}

	public JSONObject createNewAppUser(String username, String password) {
		String uri = "/AuthService!createNewAppUser.action?apiKey=" + key + "&username=" + username + "&password=" + password;

		Client client = Client.create(new DefaultClientConfig());
		WebResource webResource = client.resource(UriBuilder.fromUri(requestHost() + uri).build());
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		String jsonString = response.getEntity(String.class);
		return (JSONObject) JSONValue.parse(jsonString);
	}

	private String requestHost() {
		return appPropertyDAO.getProperty("AuthServiceHost");
	}
}
