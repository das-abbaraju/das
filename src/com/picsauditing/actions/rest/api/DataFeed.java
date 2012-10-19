package com.picsauditing.actions.rest.api;

import java.util.Map;

import org.apache.struts2.interceptor.ParameterAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.Api;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.ReportValidationException;
import com.picsauditing.actions.report.ReportData;

@SuppressWarnings("serial")
public class DataFeed extends ReportData implements ParameterAware {
	private static final Logger logger = LoggerFactory.getLogger(DataFeed.class);
	
	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(final String apiKey) {
		logger.debug("Setting apiKey = {}", apiKey);
		this.apiKey = apiKey;
	}


	@Override
	@Api
	public String execute() {
		try {
			initialize();
			runQuery();

			converter.convertForExtJS();
			json.put("data", converter.getReportResults().toJson());

			if (permissions.isAdmin() || permissions.getAdminID() > 0) {
				json.put("sql", debugSQL);
			}
			json.put("success", true);
		} catch (ReportValidationException error) {
			writeJsonError(error.getMessage());
		} catch (Exception error) {
			logger.error("Report:" + report.getId() + " " + error.getMessage() + " SQL: " + debugSQL);
			if (permissions.has(OpPerms.Debug) || permissions.getAdminID() > 0) {
				writeJsonError(error);
				json.put("sql", debugSQL);
			} else {
				writeJsonError("Invalid Query");
			}
		}

		return JSON;

	}

	@Override
	public void setParameters(Map<String, String[]> parameters) {
		logger.debug("Setting parameters");
		for (String key : parameters.keySet()) {
			logger.debug("{} = {}", key, parameters.get(key)[0].toString());
			if ("apiKey".equals(key)) {
				setApiKey(parameters.get(key)[0]);
			}
		}

	}

}
