package com.picsauditing.actions;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;

import org.json.simple.JSONObject;

import com.picsauditing.access.Anonymous;

@SuppressWarnings("serial")
public class ServerInfo extends PicsActionSupport {
	private static OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
	private static MemoryMXBean memory = ManagementFactory.getMemoryMXBean();

	@Override
	@Anonymous
	public String execute() throws Exception {
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Anonymous
	public String jsonp() {
		json = new JSONObject();
		json.put("OS Name", os.getName());
		json.put("OS Architecture", os.getArch());
		json.put("OS Version", os.getVersion());
		json.put("Available Processors", os.getAvailableProcessors());
		json.put("Load Average", os.getSystemLoadAverage());
		json.put("Heap Memory Usage", getHeapMemoryUsage() / 1000000 + " MB");
		json.put("Non-Heap Memory Usage", getNonHeapMemoryUsage() / 1000000 + " MB");
		json.put("Total Memory Usage", getTotalMemoryUsage() / 1000000 + " MB");

		return JSONP;
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