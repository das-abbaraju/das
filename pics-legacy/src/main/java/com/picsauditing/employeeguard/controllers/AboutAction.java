package com.picsauditing.employeeguard.controllers;

import com.google.gson.Gson;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.UserAgentParser;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.models.AboutModel;
import com.picsauditing.employeeguard.models.AccountType;
import com.picsauditing.employeeguard.models.ModelFactory;
import com.picsauditing.employeeguard.models.UserModel;
import com.picsauditing.employeeguard.util.DateUtil;
import com.picsauditing.search.Database;

import java.net.UnknownHostException;
import java.sql.SQLException;

public class AboutAction extends PicsRestActionSupport {
	private String operatingSystem ;
	private String browserName     ;
	private String picsEnvironment ;
	private String appServerName   ;
	private String dbServerName    ;
	private String systemTime      ;


	public String info() {
    UserAgentParser userAgentParser = new UserAgentParser(getRequest().getHeader("User-Agent"));
    operatingSystem         = userAgentParser.getBrowserOperatingSystem();
    browserName             = userAgentParser.getBrowserName();
    picsEnvironment         = super.getPicsEnvironment();
		appServerName 					= this.extractAppServerName();
		dbServerName 						= this.extractDBName();
    systemTime              = DateUtil.getSystemTime();

		AboutModel aboutModel = this.prepareAboutModel();

    jsonString = new Gson().toJson(aboutModel);
    return JSON_STRING;
  }

	private AboutModel prepareAboutModel(){
		AboutModel aboutModel = new AboutModel();
		aboutModel.setOs(operatingSystem);
		aboutModel.setBrowser(browserName);
		aboutModel.setEnvironment(picsEnvironment);
		aboutModel.setAppservername(appServerName);
		aboutModel.setDbservername(dbServerName);
		aboutModel.setTime(systemTime);

		return aboutModel;
	}

	private String extractAppServerName(){
		String appServerName;
		try {
			appServerName = java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			appServerName="Unknown Host";
		}

		return appServerName;
	}

	private String extractDBName(){
		String dbServerName;
		try {
			dbServerName=Database.getDatabaseName();
		} catch (SQLException e) {
			dbServerName="Unknown DB";
		}

		return dbServerName;
	}

	public String whoAmI() throws NoRightsException {

		AccountType accountType = AccountType.EMPLOYEE;
		if (permissions.isCorporate()) {
			accountType = AccountType.CORPORATE;
		}
		else if (permissions.isOperator()) {
			accountType = AccountType.OPERATOR;
		}
		else if(permissions.isContractor()){
			accountType = AccountType.CONTRACTOR;
		}

		UserModel userModel = ModelFactory.getUserModelFactory().create(
						permissions.getAppUserID(),
						permissions.getAccountId(),
						permissions.getName(),
						accountType);

		jsonString = new Gson().toJson(userModel);

		return JSON_STRING;
	}


}
