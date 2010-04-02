package com.picsauditing.actions;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

@SuppressWarnings("serial")
public class ServerInfo extends PicsActionSupport {
	private static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public static void setOs(OperatingSystemMXBean os) {
		ServerInfo.os = os;
	}

	public static OperatingSystemMXBean getOs() {
		return os;
	}

	public static double getLoad() {
		return os.getSystemLoadAverage();
	}
}
