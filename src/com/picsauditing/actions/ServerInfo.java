package com.picsauditing.actions;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

@SuppressWarnings("serial")
public class ServerInfo extends PicsActionSupport {
	private static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
	private static MemoryMXBean memory = ManagementFactory.getMemoryMXBean();

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

	public static MemoryMXBean getMemory() {
		return memory;
	}

	public static void setMemory(MemoryMXBean memory) {
		ServerInfo.memory = memory;
	}

	public static double getLoad() {
		return os.getSystemLoadAverage();
	}

	public static long getHeapMemoryUsage() {
		return memory.getHeapMemoryUsage().getUsed();
	}

	public static long getNonHeapMemoryUsage() {
		return memory.getNonHeapMemoryUsage().getUsed();
	}

	public static long getTotalMemoryUsage() {
		return (getNonHeapMemoryUsage() + getHeapMemoryUsage());
	}
}