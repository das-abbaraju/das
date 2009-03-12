package com.intuit.developer;

import java.util.HashMap;
import java.util.Map;

import org.jboss.util.id.GUID;

import com.picsauditing.access.LoginController;
import com.picsauditing.util.SpringUtils;

public class QBWebConnectorWorker {

	private static Map<String, QBSession> sessions = new HashMap<String, QBSession>();
	
	
	public static String authenticate( String username, String password ) throws Exception {
		LoginController loginController = (LoginController) SpringUtils.getBean("LoginController", LoginController.class);

		loginController.setUsername(username);
		loginController.setPassword(password);
		loginController.setButton("login");
		
		loginController.execute();

		return GUID.asString();		
	}
	
	
	
}
