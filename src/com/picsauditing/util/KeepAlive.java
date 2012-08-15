package com.picsauditing.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.search.Database;

public class KeepAlive {

	private final static Logger logger = LoggerFactory.getLogger(KeepAlive.class);

	private float loadFactor = 3f;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	private Database database = new Database();

	private String output;
	private boolean manualShutdown;

	public static final String SYSTEM_OK = "SYSTEM OK";
	public static final String SYSTEM_LOAD = "SYSTEM LOAD = %.2f";
	public static final String DATABASE_UNACCESSIBLE = "DATABASE UNACCESSIBLE";

	public static final String OUTPUT_JSONP = "JSONP";

	public KeepAlive(HttpServletRequest request, HttpServletResponse response, boolean manualShutdown) {
		this.request = request;
		this.response = response;
		this.manualShutdown = manualShutdown;
	}

	public String getOutput() {
		output = request.getParameter("output");
		if (OUTPUT_JSONP.equals(output)) {
			return getJsonOutput();
		}

		return getKeepAliveStatus();
	}

	@SuppressWarnings("unchecked")
	private String getJsonOutput() {
		// Assume it's JSONP for now. If we add more types we can modify
		// this
		//response.setContentType("application/jsonp");
		// response.setContentType("application/json");
		response.setContentType("application/javascript");

		JSONObject json = new JSONObject();
		json.put("status", getKeepAliveStatus());
		String callback = request.getParameter("callback");
		return callback + "(" + json.toString() + ")";
	}

	String getKeepAliveStatus() {
		if (manualShutdown) {
			return "SYSTEM NOT OK";
		}
		setLoadFactor();
		double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();

		if (systemLoadAverage > loadFactor) {
			return String.format(SYSTEM_LOAD, systemLoadAverage);
		} else if (!isDatabaseAccessible()) {
			return DATABASE_UNACCESSIBLE;
		}

		return SYSTEM_OK;
	}

	public float getLoadFactor() {
		return loadFactor;
	}

	private void setLoadFactor() {
		String[] loadFactors = request.getParameterValues("load_factor");

		try {
			if (loadFactors != null && loadFactors.length > 0) {
				loadFactor = Float.parseFloat(loadFactors[0].toString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private boolean isDatabaseAccessible() {
		try {
			return database.execute("SELECT 1");
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return false;
	}
}
