package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.management.OperatingSystemMXBean;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.search.Database;

public class KeepAliveTest {
	private KeepAlive keepAlive;
	private float loadFactor = 4f;

	@Mock
	private Database database;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private OperatingSystemMXBean operatingSystemMXBean;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		keepAlive = new KeepAlive(request, response, false);
		PicsTestUtil.forceSetPrivateField(keepAlive, "operatingSystemMXBean", operatingSystemMXBean);
		PicsTestUtil.forceSetPrivateField(keepAlive, "database", database);
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
		assertEquals(loadFactor, keepAlive.getLoadFactor(), 0.0001f);
	}

	@Test
	public void testLoadFactorIsNull() {
		when(request.getParameterValues("load_factor")).thenReturn(null);
		keepAlive.getKeepAliveStatus();
		assertEquals(loadFactor, keepAlive.getLoadFactor(), 0.0001f);
	}

	@Test
	public void testLoadFactorIsString() {
		when(request.getParameterValues("load_factor")).thenReturn(new String[] { "Hello World" });
		keepAlive.getKeepAliveStatus();
		assertEquals(loadFactor, keepAlive.getLoadFactor(), 0.0001f);
	}

	@Test
	public void testSystemLoadUnderLoadFactor() throws Exception {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(1.0);
		when(database.execute(anyString())).thenReturn(true);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
		when(request.getRequestURI()).thenReturn("/");

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
	public void testPlainText() throws Exception {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(1.0);
		when(database.execute(anyString())).thenReturn(true);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
		when(request.getRequestURI()).thenReturn("/");
		// when(request.getParameter("callback")).thenReturn("123456789");

		assertEquals(KeepAlive.SYSTEM_OK, keepAlive.getOutput());
	}

	@Test
	public void testPlainText_test2() throws Exception {
		when(operatingSystemMXBean.getSystemLoadAverage()).thenReturn(1.0);
		when(database.execute(anyString())).thenReturn(true);
		when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/"));
		when(request.getRequestURI()).thenReturn("/");
		when(request.getParameter("callback")).thenReturn("123456789");
		when(request.getParameter("output")).thenReturn(KeepAlive.OUTPUT_JSONP);

		assertEquals("123456789({\"status\":\"SYSTEM OK\"})", keepAlive.getOutput());
	}
}