package com.picsauditing.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.search.Database;

public class KeepAlive {
	
	private final static Logger logger = LoggerFactory.getLogger(KeepAlive.class);

	private float loadFactor = 3f;

	private HttpServletRequest request;
	private OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	private Database database = new Database();

	public static final String SYSTEM_OK = "SYSTEM OK";
	public static final String SYSTEM_LOAD = "SYSTEM LOAD = %.2f";
	public static final String DATABASE_UNACCESSIBLE = "DATABASE UNACCESSIBLE";

	public KeepAlive(HttpServletRequest request) {
		this.request = request;
	}

	public String getKeepAliveStatus() {
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
