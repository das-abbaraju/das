package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.lang.management.OperatingSystemMXBean;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.picsauditing.PicsTestUtil;

public class KeepAliveTest {
	private KeepAlive keepAlive;

	@Mock
	HttpServletRequest request;
	@Mock
	OperatingSystemMXBean os;

	@Before
	public void setUp() throws Exception {
		keepAlive = new KeepAlive(request);
		PicsTestUtil.forceSetPrivateField(keepAlive, "os", os);
	}

	@Test
	public void testLoadFactorIsParsedCorrectly() throws Exception {
		when(request.getParameterValues("load_factor")).thenReturn(new String[] { "1" });
		assertEquals(1.0, keepAlive.getLoadFactor(), 0.1f);
	}

	@Test
	public void testSystemLoadUnderLoadFactor() throws Exception {
	}
}
