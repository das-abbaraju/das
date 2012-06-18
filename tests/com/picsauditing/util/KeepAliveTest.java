package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.lang.management.OperatingSystemMXBean;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.search.Database;
import com.picsauditing.util.KeepAlive.URLConnector;

public class KeepAliveTest {
	private KeepAlive keepAlive;

	@Mock
	private Database database;
	@Mock
	private HttpServletRequest request;
	@Mock
	private OperatingSystemMXBean operatingSystemMXBean;
	@Mock
	private URLConnector urlConnector;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		keepAlive = new KeepAlive(request);
		PicsTestUtil.forceSetPrivateField(keepAlive, "operatingSystemMXBean", operatingSystemMXBean);
		PicsTestUtil.forceSetPrivateField(keepAlive, "database", database);
		PicsTestUtil.forceSetPrivateField(keepAlive, "urlConnector", urlConnector);
	}

	@Test
	public void testLoadFactorIsParsedCorrectly() throws Exception {
		when(request.getParameterValues("load_factor")).thenReturn(new String[] { "1" });
		keepAlive.getKeepAliveStatus();
		assertEquals(1.0, keepAlive.getLoadFactor(), 0.1f);

		when(request.getParameterValues("load_factor")).thenReturn(new String[] { "2.5" });
		keepAlive.getKeepAliveStatus();
		assertEquals(2.5, keepAlive.getLoadFactor(), 0.01f);

		when(request.getParameterValues("load_factor")).thenReturn(new String[] { "3.3333" });
		keepAlive.getKeepAliveStatus();
		assertEquals(3.3333, keepAlive.getLoadFactor(), 0.0001f);
	}

	@Test
	public void testLoadFactorIsEmpty() throws Exception {
		when(request.getParameterValues("load_factor")).thenReturn(new String[] {});
		keepAlive.getKeepAliveStatus();
		assertEquals(3.0, keepAlive.getLoadFactor(), 0.0001f);
	}

	@Test
	public void testLoadFactorIsNull() {
		when(request.getParameterValues("load_factor")).thenReturn(null);
		keepAlive.getKeepAliveStatus();
		assertEquals(3.0, keepAlive.getLoadFactor(), 0.0001f);
	}

	@Test
	public void testLoadFactorIsString() {
		when(request.getParameterValues("load_factor")).thenReturn(new String[] { "Hello World" });
		keepAlive.getKeepAliveStatus();
		assertEquals(3.0, keepAlive.getLoadFactor(), 0.0001f);
	}

	@Test
	public void testTimeoutIsParsedCorrectly() throws Exception {
		when(request.getParameterValues("timeout")).thenReturn(new String[] { "1" });
		keepAlive.getKeepAliveStatus();
		assertEquals(1.0, keepAlive.getTimeoutInSeconds(), 0.1);

		when(request.getParameterValues("timeout")).thenReturn(new String[] { "2.5" });
		keepAlive.getKeepAliveStatus();
		assertEquals(2.5, keepAlive.getTimeoutInSeconds(), 0.01);

		when(request.getParameterValues("timeout")).thenReturn(new String[] { "3.3333" });
		keepAlive.getKeepAliveStatus();
		assertEquals(3.3333, keepAlive.getTimeoutInSeconds(), 0.0001);
	}

	@Test
	public void testTimeoutIsEmpty() throws Exception {
		when(request.getParameterValues("timeout")).thenReturn(new String[] {});
		keepAlive.getKeepAliveStatus();
		assertEquals(3.0, keepAlive.getTimeoutInSeconds(), 0.0001);
	}

	@Test
	public void testTimeoutIsNull() {
		when(request.getParameterValues("timeout")).thenReturn(null);
		keepAlive.getKeepAliveStatus();
		assertEquals(3.0, keepAlive.getTimeoutInSeconds(), 0.0001);
	}

	@Test
	public void testTimeoutIsString() {
		when(request.getParameterValues("timeout")).thenReturn(new String[] { "Hello World" });
		keepAlive.getKeepAliveStatus();
		assertEquals(3.0, keepAlive.getTimeoutInSeconds(), 0.0001);
	}

	@Test
	public void testSystemLoadUnderLoadFactor() throws Exception {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(1.0);
		when(database.execute(anyString())).thenReturn(true);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
		when(request.getRequestURI()).thenReturn("/");
		when(urlConnector.connect(anyString())).thenReturn(true);

		assertEquals(KeepAlive.SYSTEM_OK, keepAlive.getKeepAliveStatus());
	}

	@Test
	public void testSystemLoadOverLoadFactor() throws Exception {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(5.0);
		assertEquals(String.format(KeepAlive.SYSTEM_LOAD, 5.0), keepAlive.getKeepAliveStatus());
	}

	@Test
	public void testDatabaseConnectionUnavailable() throws SQLException {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(1.0);
		when(database.execute(anyString())).thenReturn(false);

		assertEquals(KeepAlive.DATABASE_UNACCESSIBLE, keepAlive.getKeepAliveStatus());
	}

	@Test
	public void testDatabaseThrowsException() throws SQLException {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(1.0);
		when(database.execute(anyString())).thenThrow(new SQLException());

		assertEquals(KeepAlive.DATABASE_UNACCESSIBLE, keepAlive.getKeepAliveStatus());
	}

	@Test
	@Ignore
	// Because of PICS-6112, I am temporarily ignoring this test
	public void testPageTimedOut() throws SQLException {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(1.0);
		when(database.execute(anyString())).thenReturn(true);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
		when(request.getRequestURI()).thenReturn("/");
		when(urlConnector.connect(anyString())).thenReturn(false);

		assertEquals(KeepAlive.PAGE_TIMED_OUT, keepAlive.getKeepAliveStatus());
	}
}
