package com.picsauditing.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;

import com.picsauditing.search.Database;

public class KeepAlive {
	private float loadFactor = 3f;
	private double timeoutInSeconds = 3.0;
	private HttpServletRequest request;
	private OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

	public KeepAlive(HttpServletRequest request) {
		this.request = request;
	}

	public String getKeepAliveStatus() {
		setLoadFactor();
		String status = "";

		if (os.getSystemLoadAverage() > loadFactor) {
			status = "SYSTEM LOAD = " + os.getSystemLoadAverage();
		} else if (!isDatabaseAccessible()) {
			status = "DATABASE UNACCESSIBLE";
		} else if (!isSiteLoadedBeforeTimeout()) {
			status = "PAGE TIMED OUT";
		} else {
			status = "SYSTEM OK";
		}

		return status;
	}

	public float getLoadFactor() {
		return loadFactor;
	}

	public double getTimeoutInSeconds() {
		return timeoutInSeconds;
	}

	public void setTimeoutInSeconds(double timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
	}

	private void setLoadFactor() {
		String[] loadFactors = request.getParameterValues("load_factor");

		try {
			if (loadFactors != null && loadFactors.length > 0) {
				loadFactor = Float.parseFloat(loadFactors[0].toString());
			}
		} catch (Exception e) {
		}
	}

	private boolean isDatabaseAccessible() {
		Database db = new Database();
		boolean dbAccessible = false;

		try {
			dbAccessible = db.execute("SELECT 1");
		} catch (Exception e) {
		}

		return dbAccessible;
	}

	private boolean isSiteLoadedBeforeTimeout() {
		long now = System.currentTimeMillis();
		double diff = 999.0;

		try {
			URL organizer = new URL(determineUrlForEnvironment() + "/Login.action");
			URLConnection connection = organizer.openConnection();
			connection.connect();

			diff = (System.currentTimeMillis() - now) / 1000;
		} catch (Exception e) {
		}

		return diff < timeoutInSeconds;
	}

	private String determineUrlForEnvironment() {
		String requestURL = request.getRequestURL().toString();
		String requestURI = request.getRequestURI();

		return requestURL.replaceAll(requestURI, "");
	}
}
