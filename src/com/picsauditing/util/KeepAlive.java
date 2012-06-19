package com.picsauditing.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.SSLHandshakeException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.search.Database;

public class KeepAlive {
	private final static Logger logger = LoggerFactory.getLogger(KeepAlive.class);

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
		setTimeoutInSeconds();
		double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();

		if (systemLoadAverage > loadFactor) {
			return String.format(SYSTEM_LOAD, systemLoadAverage);
		} else if (!isDatabaseAccessible()) {
			return DATABASE_UNACCESSIBLE;
			// Because of PICS-6112, I am temporarily commenting this code out
//		} else if (!isSiteLoadedBeforeTimeout()) {
//			return PAGE_TIMED_OUT;
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

	public double getTimeoutInSeconds() {
		return timeoutInSeconds;
	}

	public void setTimeoutInSeconds() {
		String[] timeout = request.getParameterValues("timeout");

		try {
			if (timeout != null && timeout.length > 0) {
				timeoutInSeconds = Double.parseDouble(timeout[0].toString());
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

	private boolean isSiteLoadedBeforeTimeout() {
		if (urlConnector.connect(getLoginPageForEnvironment())) {
			return true;
		}

		return false;
	}

	private String getLoginPageForEnvironment() {
		String requestURL = request.getRequestURL().toString();
		String requestURI = request.getRequestURI();

		return requestURL.replaceAll(requestURI, "") + "/Login.action";
	}

	public class URLConnector {
		public boolean connect(String urlToConnect) {
			boolean sslException = false;

			try {
				URL url = new URL(urlToConnect);
				URLConnection urlConnection = url.openConnection();
				urlConnection.setReadTimeout((int) (timeoutInSeconds * 1000));
				urlConnection.setConnectTimeout((int) (timeoutInSeconds * 1000));
				urlConnection.getContent();

				return true;
			} catch (SSLHandshakeException e) {
				sslException = true;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

			if (sslException) {
				// Try HTTP instead...
				String nonSslUrl = urlToConnect.replace("https", "http");
				nonSslUrl = nonSslUrl.replace("8443", "8080");
				nonSslUrl = nonSslUrl.replace("81", "80");
				return connect(nonSslUrl);
			}

			return false;
		}
	}
}
