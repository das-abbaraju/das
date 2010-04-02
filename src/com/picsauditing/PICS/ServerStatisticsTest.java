package com.picsauditing.PICS;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import junit.framework.TestCase;

import org.jboss.util.Strings;
import org.junit.Before;

public class ServerStatisticsTest extends TestCase {
	OperatingSystemMXBean operatingSystem;

	@Before
	public void setUp() throws Exception {
		operatingSystem = ManagementFactory.getOperatingSystemMXBean();
	}

	public void testOperatingSystemExists() {
		String architecture = operatingSystem.getArch();
		String osName = operatingSystem.getName();
		String osVersion = operatingSystem.getVersion();
		int availableProcessors = operatingSystem.getAvailableProcessors();
		double loadAverage = operatingSystem.getSystemLoadAverage();

		assertTrue(operatingSystem != null);
		assertTrue(!Strings.isEmpty(architecture));
		assertTrue(!Strings.isEmpty(osName));
		assertTrue(!Strings.isEmpty(osVersion));
		assertTrue(availableProcessors > 0);
		if (osName.contains("Windows"))
			assertTrue(loadAverage == -1.0);
		else
			assertTrue(loadAverage >= 0.0);
	}
}
