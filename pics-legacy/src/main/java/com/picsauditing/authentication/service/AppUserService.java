package com.picsauditing.authentication.service;

import com.picsauditing.authentication.dao.AppUserDAO;
import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.service.AppPropertyService;
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
	@Autowired
	private AppUserDAO appUserDAO;
	private AppPropertyService appPropertyService;

	private static final String key = "1eyndgv4iddubsry9u9kheniab7r4cvb";

	public boolean isUserNameAvailable(String userName) {
		String path = "/AuthService!checkUserName.action";
		String query = "apiKey=" + key + "&username=" + userName;

		return callRESTService(path, query).contains("Available");
	}

	public JSONObject createNewAppUser(String username, String password) {
		String path = "/AuthService!createNewAppUser.action";
		String query = "apiKey=" + key + "&username=" + username + "&password=" + password;

		return (JSONObject) JSONValue.parse(callRESTService(path, query));
	}

	private String callRESTService(String path, String query) {
		String host = getRequestHost();
		UriBuilder uriBuilder = UriBuilder.fromPath(path);
		uriBuilder.replaceQuery(query);
		uriBuilder.host(host);
		uriBuilder.scheme("localhost".equals(host) ? "http" : "https");
		uriBuilder.port(("localhost".equals(host) ? getRequestHostPort() : -1));

		Client client = Client.create(new DefaultClientConfig());
		WebResource webResource = client.resource(uriBuilder.build());
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		return response.getEntity(String.class);
	}

	private String getRequestHost() {
		return appPropertyService.getPropertyString(AppProperty.AUTH_SERVICE_HOST);
	}

	private int getRequestHostPort() {
		return appPropertyService.getPropertyInt(AppProperty.AUTH_SERVICE_HOST_PORT, 8080);
	}

    public AppUser findByAppUserID(int appUserID) {
        return appUserDAO.findByAppUserID(appUserID);
    }

    public AppUser findAppUser(String userName) {
        return appUserDAO.findByUserName(userName);
    }
}
