package com.picsauditing.actions;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class ServerInfo extends PicsActionSupport implements Preparable {
	private OperatingSystemMXBean os;

	@Override
	public void prepare() throws Exception {
		os = ManagementFactory.getOperatingSystemMXBean();
	}

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public void setOs(OperatingSystemMXBean os) {
		this.os = os;
	}

	public OperatingSystemMXBean getOs() {
		return os;
	}

}
