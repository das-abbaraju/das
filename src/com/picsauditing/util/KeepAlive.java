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
	private OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	private Database database = new Database();
	private URLConnector urlConnector = new URLConnector();

	public static final String SYSTEM_OK = "SYSTEM OK";
	public static final String SYSTEM_LOAD = "SYSTEM LOAD = %.2f";
	public static final String DATABASE_UNACCESSIBLE = "DATABASE UNACCESSIBLE";
	public static final String PAGE_TIMED_OUT = "PAGE TIMED OUT";

	public KeepAlive(HttpServletRequest request) {
		this.request = request;
	}

	public String getKeepAliveStatus() {
		setLoadFactor();

		if (operatingSystemMXBean.getSystemLoadAverage() > loadFactor) {
			return String.format(SYSTEM_LOAD, operatingSystemMXBean.getSystemLoadAverage());
		} else if (!isDatabaseAccessible()) {
			return DATABASE_UNACCESSIBLE;
		} else if (!isSiteLoadedBeforeTimeout()) {
			return PAGE_TIMED_OUT;
		}

		return SYSTEM_OK;
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
		boolean dbAccessible = false;

		try {
			dbAccessible = database.execute("SELECT 1");
		} catch (Exception e) {
		}

		return dbAccessible;
	}

	private boolean isSiteLoadedBeforeTimeout() {
		long now = System.currentTimeMillis();
		double diff = 999.0;

		if (urlConnector.connect(getLoginPageForEnvironment())) {
			diff = (System.currentTimeMillis() - now) / 1000;
		}

		return diff < timeoutInSeconds;
	}

	private String getLoginPageForEnvironment() {
		String requestURL = request.getRequestURL().toString();
		String requestURI = request.getRequestURI();

		return requestURL.replaceAll(requestURI, "") + "/Login.action";
	}

	public class URLConnector {
		public boolean connect(String urlToConnect) {
			try {
				URL url = new URL(urlToConnect);
				URLConnection urlConnection = url.openConnection();
				urlConnection.connect();
			} catch (Exception e) {
				return false;
			}

			return true;
		}
	}
}
